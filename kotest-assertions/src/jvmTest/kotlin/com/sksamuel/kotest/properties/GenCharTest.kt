package com.sksamuel.kotest.properties

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forall
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.char
import io.kotest.properties.next
import io.kotest.properties.take
import io.kotest.tables.row

class GenCharTest : FunSpec({
  test("should honour seed") {
    val seed: Long? = 1234909
    val seedListA = Gen.char().random(seed).take(120).toList()
    val seedListB = Gen.char().random(seed).take(120).toList()
    seedListA shouldBe seedListB

    val ranges = listOf('A'..'L', 'P'..'Z')
    val seedListC = Gen.char(ranges).random(seed).take(120).toList()
    val seedListD = Gen.char(ranges).random(seed).take(120).toList()
    seedListC shouldBe seedListD
  }

  test("should give values evenly across all characters in ranges") {
    val genCount = 100000
    forall(
      row(listOf('A'..'A'), mapOf('A' to 1.0)),
      row(listOf('A'..'C', 'D'..'D'), mapOf('A' to 0.25, 'B' to 0.25, 'C' to 0.25, 'D' to 0.25)),
      // Overlapping ranges
      row(listOf('A'..'C', 'B'..'D'), mapOf('A' to 0.16, 'B' to 0.33, 'C' to 0.33, 'D' to 0.16)),
      // Duplicate ranges
      row(listOf('A'..'C', 'A'..'C'), mapOf('A' to 0.33, 'B' to 0.33, 'C' to 0.33))
    ) { ranges, expectedRatioMap ->
      val charGen = Gen.char(ranges)
      val actualRatioMap = (1..genCount)
        .map { charGen.next() }
        .groupBy { it }
        .map { (k, v) -> k to (v.count().toDouble() / genCount) }.toMap()

      actualRatioMap.keys shouldBe expectedRatioMap.keys
      actualRatioMap.forEach { (actualKey, actualRatio) ->
        val expectedRatio: Double = expectedRatioMap[actualKey] as Double
        actualRatio shouldBe (expectedRatio plusOrMinus 0.02)
      }
    }
  }

  test("vararg CharRange overload should give same results as List<CharRange> version") {
    val seed: Long? = 9045638172
    val listResults = Gen.char(listOf('A'..'C', 'D'..'F')).take(500, seed).toList()
    val varargResults = Gen.char('A'..'C', 'D'..'F').take(500, seed).toList()

    varargResults shouldBe listResults
  }

  test("should not allow empty ranges") {
    forall(
      row(listOf('B'..'A')),
      row(listOf('B'..'A', 'C'..'B')),
      // Last range not empty
      row(listOf('B'..'A', 'C'..'B', 'A'..'B'))
    ) { ranges ->
      shouldThrow<IllegalArgumentException> {
        Gen.char(ranges)
      }
    }
  }

  test("should not allow empty list parameter") {
    shouldThrow<IllegalArgumentException> {
      Gen.char(emptyList())
    }
  }

  test("should only give ASCII characters when no parameters given")  {
    val actualChars = Gen.char().take(100000).map(Char::toInt)
    actualChars.min() as Int shouldBeGreaterThanOrEqual 0x0021
    actualChars.max() as Int shouldBeLessThanOrEqual 0x007E
  }
})
