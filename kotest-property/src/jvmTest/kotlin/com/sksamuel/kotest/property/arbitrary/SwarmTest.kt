package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.swarm
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class SwarmTest : WordSpec({

   "Arb.swarm" should {

      "generate identical sequences for the same seed" {
         for (seed in 0..10) {
            val gen = Arb.swarm(Arb.constant("a"), Arb.constant("b"), Arb.constant("c"))
            val first = gen.samples(RandomSource.seeded(seed.toLong())).take(50).map { it.value }.toList()
            val second = gen.samples(RandomSource.seeded(seed.toLong())).take(50).map { it.value }.toList()
            first.shouldContainExactly(second)
         }
      }

      "disable a different subset of arbs for different seeds" {
         val marker = -999

         fun markerActive(seed: Long) =
            Arb.swarm(Arb.int(), Arb.constant(marker))
               .samples(RandomSource.seeded(seed))
               .take(20)
               .any { it.value == marker }

         val activeSeeds = (0..99L).filter { markerActive(it) }
         val disabledSeeds = (0..99L).filterNot { markerActive(it) }

         // some seeds keep the marker arb active for the whole run, others disable it entirely
         activeSeeds.isEmpty() shouldBe false
         disabledSeeds.isEmpty() shouldBe false
      }

      "always generate from a non-empty subset" {
         forAll(Arb.swarm(Arb.constant("a"), Arb.constant("b"), Arb.constant("c"))) {
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
