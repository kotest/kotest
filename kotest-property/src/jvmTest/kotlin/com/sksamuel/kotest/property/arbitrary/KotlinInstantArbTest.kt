package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.kotlinInstant
import io.kotest.property.arbitrary.take
import io.kotest.property.forAll
import kotlin.ranges.contains
import kotlin.time.Instant

@EnabledIf(LinuxOnlyGithubCondition::class)
class KotlinInstantArbTest : WordSpec() {
   init {
      "Arb.kotlinInstant()" should {
         "generate N valid Instants(no exceptions)" {
            Arb.kotlinInstant().take(8656) shouldHaveSize 8656
         }

         "contain edgecases" {
            Arb.kotlinInstant().edgecases() shouldContainAll listOf(
               Instant.DISTANT_PAST,
               Instant.DISTANT_FUTURE,
               Instant.fromEpochMilliseconds(0L),
            )
         }

         "respect range" {
            forAll(Arb.kotlinInstant(Instant.fromEpochMilliseconds(10000L)..Instant.fromEpochMilliseconds(20000L))) {
               it.toEpochMilliseconds() in 10000..20000
            }
         }
      }
   }
}
