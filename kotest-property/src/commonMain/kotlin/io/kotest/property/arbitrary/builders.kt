package io.kotest.property.arbitrary

import io.kotest.property.*

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), fn)

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): A? = if (edgecases.isEmpty()) null else edgecases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): A? = if (edgecases.isEmpty()) null else edgecases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs), shrinker)
}

/**
 * Creates a new [Arb] that generates edgecases from the given [edgecaseFn] function
 * and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(edgecaseFn: (RandomSource) -> A?, sampleFn: (RandomSource) -> A): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): A? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = Sample(sampleFn(rs))
   }

/**
 * Creates a new [Arb] that generates edgecases from the given [edgecaseFn] function,
 * performs shrinking using the supplied [Shrinker, and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(
   edgecaseFn: (RandomSource) -> A?,
   shrinker: Shrinker<A>,
   sampleFn: (RandomSource) -> A
): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): A? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = sampleOf(sampleFn(rs), shrinker)
   }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), shrinker, fn)

/**
 * Returns an [Arb] which repeatedly generates a single value.
 */
fun <A> Arb.Companion.constant(a: A) = element(a)
