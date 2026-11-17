package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.swarm
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class SwarmTest : WordSpec({

   "Arb.swarm" should {

      "generate identical sequences for the same seed" {
         for (seed in 0..10) {
            val gen = Arb.swarm(Arb.constant("a"), Arb.constant("b"), Arb.constant("c")).flatMap { it }
            val first = gen.samples(RandomSource.seeded(seed.toLong())).take(50).map { it.value }.toList()
            val second = gen.samples(RandomSource.seeded(seed.toLong())).take(50).map { it.value }.toList()
            first.shouldContainExactly(second)
         }
      }

      "activate a fresh subset of arbs for each generated sample" {
         val values = setOf("a", "b", "c")
         val gen = Arb.swarm(listOf(Arb.constant("a"), Arb.constant("b"), Arb.constant("c")))
         val rs = RandomSource.seeded(4242L)

         val activeSubsets = gen.samples(rs).take(50).map { sample ->
            sample.value.samples(RandomSource.seeded(0L)).take(50).map { it.value }.toSet()
         }.toList()

         activeSubsets.forEach { subset ->
            subset.isNotEmpty() shouldBe true
            subset.all { it in values } shouldBe true
         }
         activeSubsets.toSet().size shouldBeGreaterThan 1
      }

      "take its edge cases from the input arbs" {
         val a = Arb.constant("a")
         val b = Arb.constant("b")
         val c = Arb.constant("c")
         val gen = Arb.swarm(a, b, c)

         val edges = gen.edgecases(iterations = 50, rs = RandomSource.seeded(1L))
         edges.forEach { listOf(a, b, c).shouldContain(it) }
      }

      "generate edge cases from a single input arb when composed with flatMap" {
         val gen = Arb.swarm(Arb.constant("a"), Arb.constant("b")).flatMap { Arb.list(it, 1..10) }

         val edges = gen.edgecases(iterations = 100, rs = RandomSource.seeded(2L))
         edges.forEach { edge -> edge.distinct().size shouldBe 1 }
      }

      "produce both single-arb and mixed values within a single run" {
         val marker = -999
         val gen = Arb.swarm(Arb.int(), Arb.constant(marker)).flatMap { Arb.list(it, 0..100) }

         val lists = gen.samples(RandomSource.seeded(3L)).take(200).map { it.value }.toList()
         lists.any { it.all { v -> v == marker } } shouldBe true
         lists.any { it.any { v -> v != marker } } shouldBe true
      }

      "always generate from a non-empty subset" {
         forAll(Arb.swarm(Arb.constant("a"), Arb.constant("b"), Arb.constant("c")).flatMap { it }) {
            it in setOf("a", "b", "c")
         }
      }

      "reject an empty list of arbs" {
         shouldThrow<IllegalArgumentException> {
            Arb.swarm(emptyList<Arb<String>>())
         }
      }
   }
})
