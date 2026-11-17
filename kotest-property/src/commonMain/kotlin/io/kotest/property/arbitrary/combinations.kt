package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.jvm.JvmName

/**
 * An alias to [choose] to aid in discoverability for those used to Haskell's QuickCheck.
 */
fun <A> Arb.Companion.frequency(
   a: Pair<Int, A>,
   b: Pair<Int, A>,
   vararg cs: Pair<Int, A>
): Arb<A> = choose(a, b, *cs)


/**
 * Returns a stream of values based on weights:
 *
 * Arb.choose(1 to 'A', 2 to 'B') will generate 'A' 33% of the time
 * and 'B' 66% of the time.
 *
 * @throws IllegalArgumentException If any negative weight is given or only
 * weights of zero are given.
 */
fun <A> Arb.Companion.choose(a: Pair<Int, A>, b: Pair<Int, A>, vararg cs: Pair<Int, A>): Arb<A> {
   val allPairs = listOf(a, b) + cs
   val weights = allPairs.map { it.first }
   require(weights.all { it >= 0 }) { "Negative weights not allowed" }
   require(weights.any { it > 0 }) { "At least one weight must be greater than zero" }

   // The algorithm for pick is a migration of
   // the algorithm from Haskell QuickCheck
   // https://hackage.haskell.org/package/QuickCheck
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
@JvmName("frequencyArbs")
fun <A> Arb.Companion.frequency(
   a: Pair<Int, Arb<A>>,
   b: Pair<Int, Arb<A>>,
   vararg cs: Pair<Int, Arb<A>>
): Arb<A> = choose(a, b, *cs)

/**
 * Generates values from the given arbs, with ratio of values approximately in line with the given weights.
 *
 * For example: Arb.choose(1 to arbA, 2 to arbB) will generate values from arbA and arb2 with the
 * ratio approximately 1:2
 *
 * @throws IllegalArgumentException If any negative weight is given or only
 * weights of zero are given.
 */
@JvmName("chooseArbs")
fun <A> Arb.Companion.choose(a: Pair<Int, Arb<A>>, b: Pair<Int, Arb<A>>, vararg cs: Pair<Int, Arb<A>>): Arb<A> {
   val allPairs = listOf(a, b) + cs
   val allArbs = allPairs.map { it.second }
   val weights = allPairs.map { it.first }
   val total = weights.sum()
   require(weights.all { it >= 0 }) { "Negative weights not allowed" }
   require(weights.any { it > 0 }) { "At least one weight must be greater than zero" }

   // The algorithm for pick is a migration of
   // the algorithm from Haskell QuickCheck
   // https://hackage.haskell.org/package/QuickCheck
   // See function frequency in the package Test.QuickCheck
   tailrec fun pick(n: Int, l: List<Pair<Int, Arb<A>>>): Arb<A> {
      val (w, e) = l.first()
      return if (n <= w) e
      else pick(n - w, l.drop(1))
   }

   return arbitrary(
      edgecaseFn = { allArbs.edgecase(it) },
      sampleFn = { rs ->
         val n = rs.random.nextInt(1, total + 1)
         val arb = pick(n, allPairs)
         arb.sample(rs).value
      }
   )
}

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
   list.filter { _ -> it.random.nextBoolean() }
}

/**
 * Generates a random slice of the input list, including the empty list.
 * The returned list has the same order as the input list.
 */
fun <A> Arb.Companion.slice(list: List<A>): Arb<List<A>> = arbitrary {
   if (list.isEmpty()) {
      emptyList()
   } else {
      val startIndex = it.random.nextInt(0, list.size)
      val size = it.random.nextInt(0, list.size + 1)
      list.drop(startIndex).take(size)
   }
}

/**
 * Uses the [Arb]s provided to randomly generate the next element.
 * The returned [Arb]'s edge cases contains the edge cases of the input [Arb]s.
 *
 * The input must be non-empty.
 * The input [Arb]s must be infinite.
 *
 * @return A new [Arb]<A> that will randomly select values from the provided Arbs, and combine all of the provided
 * [Arb]s edge cases
 */
fun <A> Arb.Companion.choice(arb: Arb<A>, vararg arbs: Arb<A>): Arb<A> {
   val arbList = listOf(arb, *arbs)
   return arbitrary(
      edgecaseFn = { arbList.edgecase(it) },
      sampleFn = { arbList.random(it.random).next(it) }
   )
}

/**
 * Uses the [Arb]s provided to randomly generate the next element.
 * The returned [Arb]'s edge cases contains the edge cases of the input [Arb]s.
 * The input [Arb]s must be infinite.
 *
 * @throws IllegalArgumentException if no arbs have been passed to this function
 *
 * @return A new [Arb]<A> that will randomly select values from the provided [Arb]s, and combine all of the provided
 * [Arb]s edge cases
 */
fun <A> Arb.Companion.choice(arbs: List<Arb<A>>): Arb<A> {
   require(arbs.isNotEmpty()) { "List of arbs provided to Arb.choice must not be empty" }
   return arbitrary(
      edgecaseFn = { arbs.edgecase(it) },
      sampleFn = { arbs.random(it.random).next(it) }
   )
}

/**
 * Uses the [Arb]s provided to randomly generate the next element, in the spirit of swarm testing:
 * for each generated sample a random non-empty subset of the input arbs is activated and the
 * remaining arbs are disabled for that sample, each arb being activated with probability 0.5.
 *
 * Counterintuitively, disabling generators can improve a test's capacity to catch bugs: a plain
 * [choice] practically never generates e.g. a command sequence consisting solely of a single
 * command type, while a swarm of those commands will, for some samples.
 *
 * The returned [Arb] is intended to be composed with [flatMap], so that every test case of a
 * property test activates a fresh subset, for example:
 * `Arb.swarm(putArb, popArb).flatMap { Arb.list(it, 0..100) }`.
 *
 * The active subsets are drawn from the caller's random source, so replaying a failing seed
 * reproduces the same subsets. The returned [Arb]'s edge cases are the input [Arb]s themselves,
 * so when composed with [flatMap] each edge case is generated from a single input arb.
 *
 * The input must be non-empty.
 * The input [Arb]s must be infinite.
 *
 * @return A new [Arb] that will produce a randomly selected non-empty subset of the provided
 * [Arb]s on each sample
 */
fun <A> Arb.Companion.swarm(arb: Arb<A>, vararg arbs: Arb<A>): Arb<Arb<A>> = swarm(listOf(arb, *arbs))

/**
 * Uses the [Arb]s provided to randomly generate the next element, in the spirit of swarm testing:
 * for each generated sample a random non-empty subset of the input arbs is activated and the
 * remaining arbs are disabled for that sample, each arb being activated with probability 0.5.
 *
 * Counterintuitively, disabling generators can improve a test's capacity to catch bugs: a plain
 * [choice] practically never generates e.g. a command sequence consisting solely of a single
 * command type, while a swarm of those commands will, for some samples.
 *
 * The returned [Arb] is intended to be composed with [flatMap], so that every test case of a
 * property test activates a fresh subset, for example:
 * `Arb.swarm(putArb, popArb).flatMap { Arb.list(it, 0..100) }`.
 *
 * The active subsets are drawn from the caller's random source, so replaying a failing seed
 * reproduces the same subsets. The returned [Arb]'s edge cases are the input [Arb]s themselves,
 * so when composed with [flatMap] each edge case is generated from a single input arb.
 *
 * @throws IllegalArgumentException if no arbs have been passed to this function
 *
 * @return A new [Arb] that will produce a randomly selected non-empty subset of the provided
 * [Arb]s on each sample
 */
fun <A> Arb.Companion.swarm(arbs: List<Arb<A>>): Arb<Arb<A>> {
   require(arbs.isNotEmpty()) { "List of arbs provided to Arb.swarm must not be empty" }
   return arbitrary(edgecases = arbs) { rs ->
      Arb.choice(arbs.filter { rs.random.nextBoolean() }.ifEmpty { listOf(arbs.random(rs.random)) })
   }
}


