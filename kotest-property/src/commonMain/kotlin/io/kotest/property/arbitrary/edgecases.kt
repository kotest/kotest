package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Classifier
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.asSample

/**
 * Randomly chooses an [Arb] and then generates an edge case from that [Arb].
 * If the chosen arb has no edge cases, then another arb will be chosen.
 * If all [Arb]s have no edge cases, then returns null.
 */
tailrec fun <A> List<Arb<A>>.edgecase(rs: RandomSource): Sample<A>? {
   if (this.isEmpty()) return null
   val shuffled = this.shuffled(rs.random)
   return when (val edge = shuffled.first().edgecase(rs)) {
      null -> this.drop(1).edgecase(rs)
      else -> edge
   }
}

/**
 * Collects the edge cases from this arb.
 * Will stop after the given number of iterations.
 * This function is mainly used for testing.
 */
fun <A> Arb<A>.edgecases(iterations: Int = 100, rs: RandomSource = RandomSource.default()): Set<A> =
   generate(rs, EdgeConfig(edgecasesGenerationProbability = 1.0))
      .take(iterations)
      .map { it.value }
      .toSet()

/**
 * Returns a new [Arb] with the supplied edge cases replacing any existing edge cases.
 */
fun <A> Arb<A>.withEdgecases(edgecases: List<A>): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): Sample<A>? =
      if (edgecases.isEmpty()) null else edgecases.random(rs.random).asSample()

   override fun sample(rs: RandomSource): Sample<A> = this@withEdgecases.sample(rs)
   override val classifier: Classifier<out A>? = this@withEdgecases.classifier
}

/**
 * Returns a new [Arb] with the supplied edge cases replacing any existing edge cases.
 */
fun <A> Arb<A>.withEdgecases(vararg edgecases: A): Arb<A> = this.withEdgecases(edgecases.toList())

fun <A> Arb<A>.removeEdgecases(): Arb<A> = this.withEdgecases(emptyList())

/**
 * Returns a new [Arb] with the edge cases from this arb transformed by the given function [f].
 */
fun <A> Arb<A>.modifyEdgecases(f: (Sample<A>) -> Sample<A>?): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): Sample<A>? = this@modifyEdgecases.edgecase(rs)?.let(f)
   override fun sample(rs: RandomSource): Sample<A> = this@modifyEdgecases.sample(rs)
}
