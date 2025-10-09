package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.ints.beGreaterThanOrEqualTo
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.kotlinInstant
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import kotlin.time.Clock
import kotlin.time.Instant

@EnabledIf(LinuxOnlyGithubCondition::class)
class KotlinInstantArbTest : FunSpec() {
   init {
      context("Arb.kotlinInstant() should") {
         test("generate N valid Instants(no exceptions)") {
            Arb.kotlinInstant().take(8656) shouldHaveSize 8656
         }

         test("contain edgecases") {
            Arb.kotlinInstant().edgecases() shouldContainAll listOf(
               Instant.DISTANT_PAST,
               Instant.DISTANT_FUTURE,
               Instant.fromEpochMilliseconds(0L),
            )
         }
      }

      context("Arb.kotlinInstant(range) should") {
         test("throw on empty range") {
            val empty = Instant.DISTANT_FUTURE..Instant.DISTANT_PAST
            empty.isEmpty() shouldBe true
            shouldThrowExactly<NoSuchElementException> {  Arb.kotlinInstant(empty).single() }
         }

         val epoch = Instant.fromEpochSeconds(0)
         val fixedInstant = Instant.parse("2025-01-02T03:04:05.678901234Z")
         withData(
            nameFn = {"${it.start} to ${it.endInclusive}"},
            Instant.fromEpochMilliseconds(10000L)..Instant.fromEpochMilliseconds(20000L),
            Instant.fromEpochMilliseconds(20000L)..Instant.fromEpochMilliseconds(20000L),
            Instant.DISTANT_PAST..fixedInstant,
            fixedInstant..Instant.DISTANT_FUTURE,
            Instant.DISTANT_PAST..epoch,
            epoch..Instant.DISTANT_FUTURE,
            Instant.fromEpochSeconds(100L, 0)..Instant.fromEpochSeconds(100L, 999_999_999),
            Instant.fromEpochSeconds(30L, 999_999_990)..Instant.fromEpochSeconds(31, 10),
            Instant.fromEpochSeconds(100L, 300)..Instant.fromEpochSeconds(100L, 500),
            Instant.fromEpochSeconds(Long.MIN_VALUE)..Instant.fromEpochSeconds(Long.MAX_VALUE),
         ) { range ->
            val arb = Arb.kotlinInstant(range)

            test("contain edgecases") {
               val edgecases = arb.edgecases()

               edgecases shouldContain range.start
               edgecases shouldContain range.endInclusive
               if(epoch in range) edgecases shouldContain epoch
            }

            test("generates only in range") {
               checkAll(arb) { it shouldBeIn range }
            }

            test("generates reasonable coverage") {
               val nanosInRange = (range.endInclusive - range.start).inWholeNanoseconds.toInt() + 1

               arb.take(10_000).distinct().count() shouldBeGreaterThanOrEqualTo minOf(8_000, nanosInRange)
            }
         }
      }
   }
}
