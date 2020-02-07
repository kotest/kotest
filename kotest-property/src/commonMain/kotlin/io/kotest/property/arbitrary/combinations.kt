package io.kotest.property.arbitrary

import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a stream of values based on weights:
 *
 * Gen.choose(1 to 'A', 2 to 'B') will generate 'A' 33% of the time
 * and 'B' 66% of the time.
 *
 * @throws IllegalArgumentException If any negative weight is given or only
 * weights of zero are given.
 */
fun <A : Any> Arb.Companion.choose(a: Pair<Int, A>, b: Pair<Int, A>, vararg cs: Pair<Int, A>): Arb<A> {
   val allPairs = listOf(a, b) + cs
   val weights = allPairs.map { it.first }
   require(weights.all { it >= 0 }) { "Negative weights not allowed" }
   require(weights.any { it > 0 }) { "At least one weight must be greater than zero" }
   return object : Arb<A> {
      // The algorithm for pick is a migration of
      // the algorithm from Haskell QuickCheck
      // http://hackage.haskell.org/package/QuickCheck
      // See function frequency in the package Test.QuickCheck
      private tailrec fun pick(n: Int, l: Sequence<Pair<Int, A>>): A {
         val (w, e) = l.first()
         return if (n <= w) e
         else pick(n - w, l.drop(1))
      }

      override fun edgecases(): List<A> = emptyList()
      override fun sample(rs: RandomSource): Sample<A> {
         val total = weights.sum()
         val n = rs.random.nextInt(1, total + 1)
         val value = pick(n, allPairs.asSequence())
         return Sample(value)
      }
   }
}
