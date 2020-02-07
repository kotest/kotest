package io.kotest.property

import kotlin.random.Random

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
interface Gen<out A> {
   fun minIterations(): Int
   fun generate(rs: RandomSource): Sequence<Sample<A>>
}

data class RandomSource(val random: Random, val seed: Long) {
   companion object {
      val Default = RandomSource(Random.Default, 0)
   }
}

/**
 * Contains a single generated value from a [Gen] and an RTree of lazily evaluated shrinks.
 */
data class Sample<out A>(val value: A, val shrinks: RTree<A> = RTree(value))

fun <A> sampleOf(a: A, shrinker: Shrinker<A>) = Sample(a, shrinker.rtree(a))

/**
 * Returns a new [Gen] which will merge the values from this gen and the values of
 * the supplied gen together, taking one from each in turn.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen would output 1,7,2,8,3,9.
 *
 * The supplied gen must be a subtype of the type of this gen.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */

fun <A, B : A> Gen<A>.merge(other: Gen<B>): Gen<A> = object : Gen<A> {
   override fun minIterations(): Int = this@merge.minIterations() + other.minIterations()
   override fun generate(rs: RandomSource): Sequence<Sample<A>> {
      return this@merge.generate(rs).zip(other.generate(rs)).flatMap {
         sequenceOf(it.first, it.second)
      }
   }
}
