package io.kotest.property

import io.kotest.fp.NonEmptyList
import io.kotest.fp.Option
import io.kotest.property.arbitrary.of

/**
 * A [Gen] is responsible for providing values to be used in property testing. You can think of it as like
 * an input stream for values. Each arg will provide data for a specific type <A>.
 *
 * Gens can be created in two ways: with arbitrary (random) values from instances of [Arb] and
 * exhaustive values over a closed space from instances of [Exhaustive].
 *
 * Arbs generate random values across a given space. The values may be repeated, and some
 * values may never be generated at all. For example generating 1000 random integers between 0 and Int.MAX
 * will clearly not return all possible values, and some values may happen to be generated more than once.
 *
 * Exhaustives generate all values from a given space. This is useful when you want to ensure every
 * value in that space is used. For instance for enum values, it is usually more helpful to ensure each
 * enum is used, rather than picking randomly from the enums values.
 *
 * Both types of gens can be mixed and matched in property tests. For example,
 * you could test a function with 100 random positive integers (arbitrary) alongside every
 * even number from 0 to 200 (exhaustive).
 */
sealed class Gen<out A> {

   fun generate(rs: RandomSource, edgeConfig: EdgeConfig = EdgeConfig.default()): Sequence<Sample<A>> = when (this) {
      is Arb -> {
         val samples = this.samples(rs).iterator()
         generateSequence {
            this.edges().fold(
               { samples.next() },
               { edge ->
                  val p = rs.random.nextDouble(0.0, 1.0)
                  if (p < edgeConfig.edgecasesGenerationProbability)
                     Sample(edge.generate(rs, edgeConfig))
                  else
                     samples.next()
               }
            )
         }
      }
      is Exhaustive -> {
         check(this.values.isNotEmpty()) { "Exhaustive.values shouldn't be a empty list." }

         generateSequence { this.values.map { Sample(it) } }.flatten()
      }
   }

   /**
    * The minimum iteration count required for this [Gen] to be invoked.
    * Requesting a property test with fewer than this will result in an exception.
    */
   fun minIterations(): Int = when (this) {
      is Arb -> 1
      is Exhaustive -> this.values.size
   }
}

/**
 * An [Arb] (for arbitrary) is a provider of property test data in two categories: edgecases and samples.
 *
 * Edge cases are values that are a common source of bugs. For example, a function using ints is
 * more likely to fail for common edge cases like zero, minus 1, positive 1, [Int.MAX_VALUE] and [Int.MIN_VALUE]
 * rather than random values like 965489. Therefore, edge cases can be included in sequences
 * generated by an arbitrary.
 *
 * Not all arbitraries will utilize edge cases. For example, if you define an integer generator
 * using a subset of the number space - say from 100 to 250,000 - then no edge cases are provided.
 *
 * Samples are chosen randomly from the sample space and are used to give a greater breadth to
 * the test cases. For example, in the case of a function using integers, these random values
 * could be from across the entire integer number line, or could be limited to a subset of ints
 * such as natural numbers or even numbers.
 *
 * In order to use an [Arb] inside a property test, one must invoke the [take] method, passing in the
 * number of iterations required and optionally a [ShrinkingMode].
 */
abstract class Arb<out A> : Gen<A>() {

   /**
    * Edgecase values for this type A.
    */
   abstract fun edgecases(): List<A>

   internal open fun edges(): Option<Edgecase<A>> =
      NonEmptyList
         .fromList(edgecases())
         .map { Edgecase { rs, _ -> it.all.random(rs.random) } }

   /**
    * Returns a random [Sample] from this [Arb] using the supplied random source.
    */
   open fun sample(rs: RandomSource): Sample<A> = values(rs).first()

   @Deprecated("implement one value at a time using sample(rs). This function will be removed in 4.6", ReplaceWith("sample(rs)"))
   open fun values(rs: RandomSource): Sequence<Sample<A>> = emptySequence()

   /**
    * Returns a sequence from values generated from this arb.
    * Edgecases will be ignored.
    */
   fun samples(rs: RandomSource = RandomSource.Default): Sequence<Sample<A>> {
      val valuesIterator = values(rs).iterator()
      return if (valuesIterator.hasNext()) {
         generateSequence { valuesIterator.next() }
      } else {
         generateSequence { sample(rs) }
      }
   }

   companion object
}

/**
 * An exhaustive is a type of [Gen] which generates an exhaustive set of values from a defined range.
 *
 * An example of a exhaustive is the sequence of integers from 0 to 100.
 * Another example is all strings of two characters.
 *
 * A progression is useful when you want to generate an exhaustive set of values from a given
 * sample space, rather than random values from that space. For example, if you were testing a
 * function that used an enum, you might prefer to guarantee that every enum value is used, rather
 * than selecting randomly from amongst the enum values (with possible duplicates and gaps).
 *
 * Exhaustives do not shrink their values. There is no need to find a smaller failing case, because
 * the smaller values will themselves naturally be included in the tested values.
 *
 * An exhaustive is less suitable when you have a large sample space you need to select values from.
 */
abstract class Exhaustive<out A> : Gen<A>() {

   /**
    * Returns the values of this [Exhaustive].
    */
   abstract val values: List<A>

   /**
    * Converts this into an [Arb] where the generated values of the returned arb
    * are choosen randomly from the values provided by this exhausive.
    */
   fun toArb(): Arb<A> = Arb.of(values)

   companion object
}

/**
 * Contains a single generated value from a [Gen] and an RTree of lazily evaluated shrinks.
 */
data class Sample<out A>(val value: A, val shrinks: RTree<A> = RTree({ value }))

/**
 * Returns a [Sample] with shrinks by using the supplied [Shrinker] against the input value [a].
 */
fun <A> sampleOf(a: A, shrinker: Shrinker<A>) = Sample(a, shrinker.rtree(a))

internal data class Edgecase<out A>(val generate: (rs: RandomSource, config: EdgeConfig) -> A) {
   constructor(generate: (RandomSource) -> A) : this({ rs, _ -> generate(rs) })

   companion object;

   fun <B> map(fn: (A) -> B): Edgecase<B> = Edgecase { rs, config ->
      fn(this@Edgecase.generate(rs, config))
   }

   fun <B> flatMap(fn: (A) -> Edgecase<B>): Edgecase<B> = Edgecase { rs, config ->
      this@Edgecase.generate(rs, config)
         .let(fn)
         .generate(rs, config)
   }

   fun plus(other: Edgecase<@UnsafeVariance A>): Edgecase<A> = Edgecase { rs, config ->
      if (rs.random.nextBoolean()) this@Edgecase.generate(rs, config) else other.generate(rs, config)
   }
}

data class EdgeConfig(
   val determinism: Double = PropertyTesting.edgecasesBindDeterminism,
   val edgecasesGenerationProbability: Double = PropertyTesting.edgecasesGenerationProbability
) {
   companion object;

   init {
      check(determinism in 0.0..1.0) { "provided determinism $determinism is not between 0.0 and 1.0" }
      check(edgecasesGenerationProbability in 0.0..1.0) {
         "provided edgecasesProbability $edgecasesGenerationProbability is not between 0.0 and 1.0"
      }
   }
}
