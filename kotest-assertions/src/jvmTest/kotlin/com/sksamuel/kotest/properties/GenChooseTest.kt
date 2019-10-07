package com.sksamuel.kotest.properties

import io.kotest.data.forall
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.properties.Gen
import io.kotest.properties.choose
import io.kotest.properties.next
import io.kotest.shouldBe
import io.kotest.shouldNotThrow
import io.kotest.shouldThrow
import io.kotest.specs.FunSpec
import io.kotest.tables.row

class GenChooseTest : FunSpec({
  test("<Int, Int> should give values between min and max inclusive") {
    // Test parameters include the test for negative bounds
    forall(
      row(-10, -1),
      row(1, 3),
      row(-100, 100),
      row(Int.MAX_VALUE - 10, Int.MAX_VALUE),
      row(Int.MIN_VALUE, Int.MIN_VALUE + 10)
    ) { vMin, vMax ->
      val expectedValues = (vMin..vMax).toSet()
      val actualValues = (1..100000).map { Gen.choose(vMin, vMax).next() }.toSet()

      actualValues shouldBe expectedValues
    }
  }

  test("<Long, Long> should give values between min and max inclusive") {
    // Test parameters include the test for negative bounds
    forall(
      row(-10L, -1L),
      row(1L, 3L),
      row(-100L, 100L),
      row(Long.MAX_VALUE - 10L, Long.MAX_VALUE),
      row(Long.MIN_VALUE, Long.MIN_VALUE + 10L)
    ) { vMin, vMax ->
      val expectedValues = (vMin..vMax).toSet()
      val actualValues = (1..100000).map { Gen.choose(vMin, vMax).next() }.toSet()

      actualValues shouldBe expectedValues
    }
  }

  test("weighted should honour seed") {
    val seed = 684658365846L
    val seedListA = Gen.choose(1 to 'A', 3 to 'B', 4 to 'C', 5 to 'D').random(seed).take(500).toList()
    val seedListB = Gen.choose(1 to 'A', 3 to 'B', 4 to 'C', 5 to 'D').random(seed).take(500).toList()

    seedListA shouldBe seedListB
  }

  test("weighted should generate expected values in correct ratios according to weights") {
    forall(
      row(listOf(1 to 'A', 1 to 'B'), mapOf('A' to 0.5, 'B' to 0.5)),
      row(listOf(1 to 'A', 3 to 'B', 1 to 'C'), mapOf('A' to 0.2, 'B' to 0.6, 'C' to 0.2)),
      row(listOf(1 to 'A', 3 to 'C', 1 to 'C'), mapOf('A' to 0.2, 'C' to 0.8)),
      row(listOf(1 to 'A', 3 to 'B', 1 to 'C', 4 to 'D'), mapOf('A' to 0.11, 'B' to 0.33, 'C' to 0.11, 'D' to 0.44))
    ) { weightPairs, expectedRatiosMap ->
      val genCount = 100000
      val chooseGen = Gen.choose(weightPairs[0], weightPairs[1], *weightPairs.drop(2).toTypedArray())
      val actualCountsMap = (1..genCount).map { chooseGen.next() }.groupBy { it }.map { (k, v) -> k to v.count() }
      val actualRatiosMap = actualCountsMap.map { (k, v) -> k to (v.toDouble() / genCount) }.toMap()

      actualRatiosMap.keys shouldBe expectedRatiosMap.keys

      actualRatiosMap.forEach { (k, actualRatio) ->
        actualRatio shouldBe (expectedRatiosMap[k] as Double plusOrMinus 0.02)
      }
    }
  }

  test("weighted should not accept negative weights") {
    shouldThrow<IllegalArgumentException> { Gen.choose(-1 to 'A', 1 to 'B') }
  }

  test("weighted should not accept all zero weights") {
    shouldThrow<IllegalArgumentException> { Gen.choose(0 to 'A', 0 to 'B') }
  }

  test("weighted should accept weights if at least one is non-zero") {
    shouldNotThrow<Exception> { Gen.choose(0 to 'A', 0 to 'B', 1 to 'C') }
  }
})