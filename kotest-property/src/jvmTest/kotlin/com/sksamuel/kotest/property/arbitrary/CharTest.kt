package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.take
import io.kotest.property.random

@EnabledIf(LinuxOnlyGithubCondition::class)
class CharTest : FunSpec({

   test("should honour seed") {
      val seedListA = Arb.char().samples(1234909L.random()).take(120).toList().map { it.value }
      val seedListB = Arb.char().samples(1234909L.random()).take(120).toList().map { it.value }
      seedListA shouldBe seedListB

      val ranges = listOf('A'..'L', 'P'..'Z')
      val seedListC = Arb.char(ranges).samples(1234909L.random()).take(120).toList().map { it.value }
      val seedListD = Arb.char(ranges).samples(1234909L.random()).take(120).toList().map { it.value }
      seedListC shouldBe seedListD
   }

   test("should give values evenly across all characters in ranges") {
      val iterations = 100000
      forAll(
         row(listOf('A'..'A'), mapOf('A' to 1.0)),
         row(listOf('A'..'C', 'D'..'D'), mapOf('A' to 0.25, 'B' to 0.25, 'C' to 0.25, 'D' to 0.25)),
         // Overlapping ranges
         row(listOf('A'..'C', 'B'..'D'), mapOf('A' to 0.16, 'B' to 0.33, 'C' to 0.33, 'D' to 0.16)),
         // Duplicate ranges
         row(listOf('A'..'C', 'A'..'C'), mapOf('A' to 0.33, 'B' to 0.33, 'C' to 0.33))
      ) { ranges, expectedRatioMap ->
         val charGen = Arb.char(ranges)
         val actualRatioMap = (1..iterations)
            .map { charGen.next() }
            .groupBy { it }
            .map { (k, v) -> k to (v.count().toDouble() / iterations) }.toMap()

         actualRatioMap.keys shouldBe expectedRatioMap.keys
         actualRatioMap.forEach { (actualKey, actualRatio) ->
            val expectedRatio: Double = expectedRatioMap[actualKey] as Double
            actualRatio shouldBe (expectedRatio plusOrMinus 0.02)
         }
      }
   }

   test("vararg CharRange overload should give same results as List<CharRange> version") {
      val listResults = Arb.char(listOf('A'..'C', 'D'..'F')).take(500, 9045638172L.random()).toList()
      val varargResults = Arb.char('A'..'C', 'D'..'F').take(500, 9045638172L.random()).toList()
      varargResults shouldBe listResults
   }

   test("should not allow empty ranges") {
      forAll(
         row(listOf('B'..'A')),
         row(listOf('B'..'A', 'C'..'B')),
         // Last range not empty
         row(listOf('B'..'A', 'C'..'B', 'A'..'B'))
      ) { ranges ->
         shouldThrow<IllegalArgumentException> {
            Arb.char(ranges)
         }
      }
   }

   test("should not allow empty list parameter") {
      shouldThrow<IllegalArgumentException> {
         Arb.char(emptyList())
      }
   }

   test("should only give ASCII characters when no parameters given") {
      val actualChars = Arb.char().take(100000).map(Char::code).toList()
      actualChars.minOrNull() as Int shouldBeGreaterThanOrEqual 0x0021
      actualChars.maxOrNull() as Int shouldBeLessThanOrEqual 0x007E
   }
})
