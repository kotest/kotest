package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.map
import io.kotest.property.sampleOf

/**
 * Returns a sequence of size [count] from values generated from this arb.
 * Edgecases will be ignored.
 */
fun <A> Arb<A>.take(count: Int, rs: RandomSource = RandomSource.Default): Sequence<A> =
   samples(rs).map { it.value }.take(count)

/**
 * Returns a single value generated from this arb ignoring edgecases.
 */
fun <A> Arb<A>.single(rs: RandomSource = RandomSource.Default): A = this.samples(rs).map { it.value }.first()
fun <A> Arb<A>.next(rs: RandomSource = RandomSource.Default): A = single(rs)

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and generates values from the given function.
 */
fun <A> arbitrary(fn: (RandomSource) -> A) = object : Arb<A>() {
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { Sample(fn(rs)) }
   override fun edgecases(): List<A> = emptyList()
}

/**
 * Creates a new [Arb] that performs no shrinking, uses the supplied edge case values,
 * and generates values from the given function that is invoked once to return a sequence of values.
 */
@Deprecated(
   "This function will no longer accept Sequence<A>. Use Arb.create or arb with (RandomSource -> A) for compatibility. This function will be removed in 4.5."
)
fun <A> arb(edgecases: List<A> = emptyList(), f: (RandomSource) -> Sequence<A>) = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun values(rs: RandomSource): Sequence<Sample<A>> = f(rs).map { Sample(it) }
   override fun sample(rs: RandomSource): Sample<A> = Sample(f(rs).first())
}

/**
 * Creates a new [Arb] that performs shrinking using the supplier shrinker, uses the
 * supplied edge case values, and provides values from sequence returning function.
 */
@Deprecated(
   "This function will no longer accept Sequence<A>. Use Arb.create or arb with (RandomSource -> A) for compatibility. This function will be removed in 4.5."
)
fun <A> arb(
   edgecases: List<A> = emptyList(),
   shrinker: Shrinker<A>,
   f: (RandomSource) -> Sequence<A>
) = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun values(rs: RandomSource): Sequence<Sample<A>> = f(rs).map { sampleOf(it, shrinker) }
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(f(rs).first(), shrinker)
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied shrinker and generates each value
 * from successive invocations of the given function f.
 */
fun <A> arb(
   shrinker: Shrinker<A>,
   f: (RandomSource) -> A
) = object : Arb<A>() {
   override fun edgecases(): List<A> = emptyList()
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { sampleOf(f(rs), shrinker) }
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(f(rs), shrinker)
}

/**
 * Creates a new [Arb] with the given edgecases, that performs shrinking using the supplied shrinker and
 * generates each value from successive invocations of the given function f.
 */
fun <A> arb(
   shrinker: Shrinker<A>,
   edgecases: List<A> = emptyList(),
   f: (RandomSource) -> A
) = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { sampleOf(f(rs), shrinker) }
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(f(rs), shrinker)
}

/**
 * Returns an [Arb] where each value is generated from the given function.
 */
fun <A> Arb.Companion.create(edgeCases: List<A> = emptyList(), fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { Sample(fn(rs)) }
   override fun edgecases(): List<A> = edgeCases
}

/**
 * Returns a new [Arb] which takes its elements from the receiver and filters them using the supplied
 * predicate. This gen will continue to request elements from the underlying gen until one satisfies
 * the predicate.
 */
fun <A> Arb<A>.filter(predicate: (A) -> Boolean) = object : Arb<A>() {
   override fun edgecases(): List<A> = this@filter.edgecases().filter(predicate)
   override fun values(rs: RandomSource): Sequence<Sample<A>> = this@filter.values(rs).filter { predicate(it.value) }
   override fun sample(rs: RandomSource): Sample<A> = samples(rs).filter { predicate(it.value) }.first()
}

/**
 * @return a new [Arb] by filtering this arbs output by the negated function [f]
 */
fun <A> Arb<A>.filterNot(f: (A) -> Boolean): Arb<A> = filter { !f(it) }

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.map(f: (A) -> B): Arb<B> = object : Arb<B>() {
   override fun edgecases(): List<B> {

      val edges = this@map.edgecases()
      return if (edges.isEmpty()) emptyList() else edges.map(f)
   }

   override fun values(rs: RandomSource): Sequence<Sample<B>> {
      return this@map.values(rs).map { Sample(f(it.value), it.shrinks.map(f)) }
   }

   override fun sample(rs: RandomSource): Sample<B> = Sample(f(this@map.sample(rs).value))
}

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.flatMap(f: (A) -> Arb<B>): Arb<B> = object : Arb<B>() {
   override fun edgecases(): List<B> = this@flatMap.edgecases().flatMap { f(it).edgecases() }
   override fun values(rs: RandomSource): Sequence<Sample<B>> =
      this@flatMap.samples(rs).zip(generateSequence { rs.random.nextLong() }) { sample, nextLong ->
         Sample(f(sample.value).next(RandomSource.seeded(nextLong)))
      }

   override fun sample(rs: RandomSource): Sample<B> = f(this@flatMap.sample(rs).value).sample(rs)
}

/**
 * Returns a new [Arb] which ensures only unique values are generated by keeping track of previously
 * generated values.
 *
 * Note: This arb can result in an infinite loop if more elements are requested than can be generated uniquely.
 */
fun <A> Arb<A>.distinct() = distinctBy { it }

/**
 * Returns a new [Arb] which ensures only distinct keys returned by the given [selector] function are generated by
 * keeping track of previously generated values.
 *
 * Note: This arb can result in an infinite loop if more elements are requested than can be generated uniquely.
 */
fun <A, B> Arb<A>.distinctBy(selector: (A) -> B) = object : Arb<A>() {
   override fun edgecases(): List<A> = this@distinctBy.edgecases().distinctBy(selector)

   override fun values(rs: RandomSource): Sequence<Sample<A>> {
      return this@distinctBy.values(rs).distinctBy { selector(it.value) }
   }

   override fun sample(rs: RandomSource): Sample<A> =
      samples(rs).distinctBy { selector(it.value) }.first()
}

fun <A> Arb.Companion.constant(a: A) = element(a)

/**
 * Returns a new [Arb] which will merge the values from this Arb and the values of
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
fun <A, B : A> Arb<A>.merge(other: Gen<B>): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = when (other) {
      is Arb -> this@merge.edgecases().zip(other.edgecases()).flatMap { listOf(it.first, it.second) }
      is Exhaustive -> this@merge.edgecases()
   }

   override fun values(rs: RandomSource): Sequence<Sample<A>> {
      val aIterator = this@merge.samples(rs).iterator()
      val bIterator = when (other) {
         is Arb -> other.samples(rs).iterator()
         is Exhaustive -> other.toArb().samples(rs).iterator()
      }
      return generateSequence {
         if (rs.random.nextBoolean()) aIterator.next() else bIterator.next()
      }
   }

   override fun sample(rs: RandomSource): Sample<A> =
      if (rs.random.nextBoolean()) {
         this@merge.sample(rs)
      } else {
         when (other) {
            is Arb -> other.sample(rs)
            is Exhaustive -> other.toArb().sample(rs)
         }
      }
}
