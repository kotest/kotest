package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Randomly chooses an [Arb] and then generates an edge case from that [Arb].
 * If the chosen arb has no edge cases, then another arb will be chosen.
 * If all [Arb]s have no edge cases, then returns null.
 */
tailrec fun <A> List<Arb<A>>.edgeCase(rs: RandomSource): A? {
   if (this.isEmpty()) return null
   val shuffled = this.shuffled(rs.random)
   return when (val edge = shuffled.first().edgeCase(rs)) {
      null -> this.drop(1).edgeCase(rs)
      else -> edge
   }
}

@Deprecated("use edgeCase", ReplaceWith("edgeCase"))
fun <A> List<Arb<A>>.edgecase(rs: RandomSource): A? = edgeCase(rs)

/**
 * Collects the edge cases from this arb.
 * Will stop after the given number of iterations.
 * This function is mainly used for testing.
 */
fun <A> Arb<A>.edgeCases(iterations: Int = 100, rs: RandomSource = RandomSource.default()): Set<A> =
   generate(rs, EdgeConfig(edgeCasesGenerationProbability = 1.0))
      .take(iterations)
      .map { it.value }
      .toSet()

@Deprecated("use edgeCases", ReplaceWith("edgeCases"))
fun <A> Arb<A>.edgecases(iterations: Int = 100, rs: RandomSource = RandomSource.default()): Set<A> =
   edgeCases(iterations, rs)

/**
 * Returns a new [Arb] with the supplied edge cases replacing any existing edge cases.
 */
fun <A> Arb<A>.withEdgeCases(edgeCases: List<A>): Arb<A> = arbitrary(edgeCases) { this.next(it) }

@Deprecated("use withEdgeCases", ReplaceWith("withEdgeCases"))
fun <A> Arb<A>.withEdgecases(edgecases: List<A>): Arb<A> = withEdgeCases(edgecases)

/**
 * Returns a new [Arb] with the supplied edge cases replacing any existing edge cases.
 */
fun <A> Arb<A>.withEdgeCases(vararg edgeCases: A): Arb<A> = withEdgeCases(edgeCases.toList())

@Deprecated("use withEdgeCases", ReplaceWith("withEdgeCases"))
fun <A> Arb<A>.withEdgecases(vararg edgecases: A): Arb<A> = withEdgeCases(edgecases.toList())

/**
 * Returns a new [Arb] with the edge cases from this arb transformed by the given function [f].
 */
fun <A> Arb<A>.modifyEdgeCases(f: (A) -> A?): Arb<A> = object : Arb<A>() {
   override fun edgeCase(rs: RandomSource): A? = this@modifyEdgeCases.edgeCase(rs)?.let(f)
   override fun sample(rs: RandomSource): Sample<A> = this@modifyEdgeCases.sample(rs)
}

@Deprecated("use modifyEdgeCases", ReplaceWith("modifyEdgeCases"))
fun <A> Arb<A>.modifyEdgecases(f: (A) -> A?): Arb<A> = modifyEdgeCases(f)
