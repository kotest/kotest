package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import kotlin.jvm.JvmName

/**
 * Returns a stream of values based on weights:
 *
 * Arb.choose(1 to 'A', 2 to 'B') will generate 'A' 33% of the time
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

   // The algorithm for pick is a migration of
   // the algorithm from Haskell QuickCheck
   // http://hackage.haskell.org/package/QuickCheck
   // See function frequency in the package Test.QuickCheck
   tailrec fun pick(n: Int, l: Sequence<Pair<Int, A>>): A {
      val (w, e) = l.first()
      return if (n <= w) e
      else pick(n - w, l.drop(1))
   }

   return arbitrary {
      val total = weights.sum()
      val n = it.random.nextInt(1, total + 1)
      pick(n, allPairs.asSequence())
   }
}

/**
 * An alias to [choose] to aid in discoverability for those used to Haskell's QuickCheck.
 */
fun <A : Any> Arb.Companion.frequency(
   a: Pair<Int, A>,
   b: Pair<Int, A>,
   vararg cs: Pair<Int, A>
): Arb<A> = choose(a, b, *cs)

/**
 * Returns a stream of values based on weights:
 *
 * Arb.choose(1 to arbA, 2 to arbB) will generate a value from arbA 33% of the time
 * and from arbB 66% of the time.
 *
 * @throws IllegalArgumentException If any negative weight is given or only
 * weights of zero are given.
 */
@JvmName("chooseArbs")
fun <A : Any> Arb.Companion.choose(a: Pair<Int, Arb<A>>, b: Pair<Int, Arb<A>>, vararg cs: Pair<Int, Arb<A>>): Arb<A> {
   val allPairs = listOf(a, b) + cs
   val weights = allPairs.map { it.first }
   val total = weights.sum()
   require(weights.all { it >= 0 }) { "Negative weights not allowed" }
   require(weights.any { it > 0 }) { "At least one weight must be greater than zero" }

   // The algorithm for pick is a migration of
   // the algorithm from Haskell QuickCheck
   // http://hackage.haskell.org/package/QuickCheck
   // See function frequency in the package Test.QuickCheck
   tailrec fun pick(n: Int, l: List<Pair<Int, Arb<A>>>): Arb<A> {
      val (w, e) = l.first()
      return if (n <= w) e
      else pick(n - w, l.drop(1))
   }

   return arbitrary(
      edgecaseGenerator = { rs ->
         val n = rs.random.nextInt(1, total + 1)
         val arb = pick(n, allPairs)
         arb.generateEdgecase(rs)
      },
      sampleGenerator = { rs ->
         val n = rs.random.nextInt(1, total + 1)
         val arb = pick(n, allPairs)
         arb.single(rs)
      }
   )
}

/**
 * An alias to [choose] to aid in discoverability for those used to Haskell's QuickCheck.
 */
@JvmName("frequencyArbs")
fun <A : Any> Arb.Companion.frequency(
   a: Pair<Int, Arb<A>>,
   b: Pair<Int, Arb<A>>,
   vararg cs: Pair<Int, Arb<A>>
): Arb<A> = choose(a, b, *cs)

/**
 * Generates random permutations of a list.
 */
fun <A> Arb.Companion.shuffle(list: List<A>): Arb<List<A>> = arbitrary {
   list.shuffled(it.random)
}

/**
 * Generates a random subsequence of the input list, including the empty list.
 * The returned list has the same order as the input list.
 */
fun <A> Arb.Companion.subsequence(list: List<A>): Arb<List<A>> = arbitrary {
   val size = it.random.nextInt(0, list.size + 1)
   list.take(size)
}

/**
 * Randomly selects one of the given gens to generate the next element.
 * The input must be non-empty.
 * The input gens must be infinite.
 */
@Deprecated(
   message = "Deprecated in favor of a function that returns an Arb instead of a Gen. Will be removed in 4.6",
   replaceWith = ReplaceWith("Arb.Companion.choice(vararg arbs: Arb<A>)")
)
fun <A> Arb.Companion.choice(vararg gens: Gen<A>): Gen<A> {
   check(gens.isNotEmpty()) { "at least one gen needs to be provided for choice" }

   val arbs = gens.toList().map { gen ->
      when (gen) {
         is Arb -> gen
         is Exhaustive -> gen.toArb()
      }
   }

   return choice(arbs.first(), *arbs.drop(1).toTypedArray())
}

/**
 * Uses the Arbs provided to randomly generate the next element.
 * The returned Arb.edgecases() contains all of edgecases of the provided arbs
 * The input must be non-empty.
 * The input arbs must be infinite.
 *
 * @throws IllegalArgumentException if no arbs have been passed to this function
 * @return A new Arb<A> that will randomly select values from the provided Arbs, and combine all of the provided
 * Arbs edgecases
 */
fun <A> Arb.Companion.choice(arb: Arb<A>, vararg arbs: Arb<A>): Arb<A> {
   val arbList = listOf(arb, *arbs)
   return arbitrary(
      edgecaseGenerator = { rs -> arbList.random(rs.random).generateEdgecase(rs) },
      sampleGenerator = { rs -> arbList.random(rs.random).next(rs) }
   )
}
