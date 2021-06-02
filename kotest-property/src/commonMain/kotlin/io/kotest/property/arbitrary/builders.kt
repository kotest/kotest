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
fun <A> arbitrary(edgeCases: List<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgeCase(rs: RandomSource): A? = if (edgeCases.isEmpty()) null else edgeCases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgeCases: List<A>, shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgeCase(rs: RandomSource): A? = if (edgeCases.isEmpty()) null else edgeCases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs), shrinker)
}

/**
 * Creates a new [Arb] that generates edge cases from the given [edgeCaseFn] function
 * and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(edgeCaseFn: (RandomSource) -> A?, sampleFn: (RandomSource) -> A): Arb<A> =
   object : Arb<A>() {
      override fun edgeCase(rs: RandomSource): A? = edgeCaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = Sample(sampleFn(rs))
   }

/**
 * Creates a new [Arb] that generates edge cases from the given [edgeCaseFn] function,
 * performs shrinking using the supplied [Shrinker, and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(
   edgeCaseFn: (RandomSource) -> A?,
   shrinker: Shrinker<A>,
   sampleFn: (RandomSource) -> A
): Arb<A> =
   object : Arb<A>() {
      override fun edgeCase(rs: RandomSource): A? = edgeCaseFn(rs)
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
