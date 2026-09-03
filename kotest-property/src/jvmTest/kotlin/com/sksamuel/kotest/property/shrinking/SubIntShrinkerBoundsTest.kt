package com.sksamuel.kotest.property.shrinking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.bytes.shouldBeBetween
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.short.shouldBeBetween
import io.kotest.property.Arb
import io.kotest.property.RTree
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.uByte
import io.kotest.property.arbitrary.uShort

/**
 * The sub 32 bit integral arbs used to pair a range constrained generator with a shrinker bounded
 * by the full type range, so shrinking could report a "minimal" value the arb could never produce.
 * See https://github.com/kotest/kotest/issues/6219
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class SubIntShrinkerBoundsTest : FunSpec({

   test("Arb.short shrinks should stay within the generator bounds") {
      shrinkCandidates(Arb.short(100, 200)).forAll { it.shouldBeBetween(100, 200) }
   }

   test("Arb.short shrinks of a negative range should stay within the generator bounds") {
      shrinkCandidates(Arb.short(-200, -100)).forAll { it.shouldBeBetween(-200, -100) }
   }

   test("Arb.byte shrinks should stay within the generator bounds") {
      shrinkCandidates(Arb.byte(50, 100)).forAll { it.shouldBeBetween(50, 100) }
   }

   test("Arb.byte shrinks of a negative range should stay within the generator bounds") {
      shrinkCandidates(Arb.byte(-100, -50)).forAll { it.shouldBeBetween(-100, -50) }
   }

   test("Arb.uShort shrinks should stay within the generator bounds") {
      shrinkCandidates(Arb.uShort(100u, 200u)).forAll { it.shouldBeBetween(100u, 200u) }
   }

   test("Arb.uByte shrinks should stay within the generator bounds") {
      shrinkCandidates(Arb.uByte(50u, 100u)).forAll { it.shouldBeBetween(50u, 100u) }
   }
})

/**
 * Draws [samples] values from [arb] and returns every candidate found in the shrink trees of those
 * samples, traversed [depth] levels deep. The traversal is depth limited because the shrink tree of
 * an integral value fans out quickly.
 */
private fun <A> shrinkCandidates(arb: Arb<A>, samples: Int = 50, depth: Int = 3): List<A> {
   val rs = RandomSource.seeded(1234L)
   val candidates = arb.samples(rs).take(samples).flatMap { collect(it.shrinks, depth) }.toList()
   candidates.shouldNotBeEmpty()
   return candidates
}

private fun <A> collect(tree: RTree<A>, depth: Int): List<A> =
   if (depth == 0) emptyList()
   else tree.children.value.flatMap { listOf(it.value()) + collect(it, depth - 1) }
