package com.sksamuel.kotlintest.properties

import io.kotlintest.data.forall
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.properties.Gen
import io.kotlintest.properties.choose
import io.kotlintest.properties.next
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.row
import java.lang.IllegalArgumentException

class ChooseGenTest : FunSpec({
  test("should generate expected values in correct ratios according to weights") {
    forall(
      row(listOf(1 to 'A', 1 to 'B'), listOf(0.5, 0.5)),
      row(listOf(1 to 'A', 3 to 'B', 1 to 'C'), listOf(0.2, 0.6, 0.2)),
      row(listOf(1 to 'A', 3 to 'C', 1 to 'C'), listOf(0.2, 0.8)),
      row(listOf(1 to 'A', 3 to 'B', 1 to 'C', 4 to 'D'), listOf(0.11, 0.33, 0.11, 0.44))
    ) { pairs, expectedRatios ->
      val expectedUniqueValues = pairs.map { it.second }.toSet()
      val chooseGen = Gen.choose(pairs[0], pairs[1], *pairs.drop(2).toTypedArray())

      // Sorted map so that the actual ratios will have
      // the same order as the expected ratios
      val actualCountMap = sortedMapOf(*pairs.map { it.second to 0 }.toTypedArray())
      val allGenValues = (1..100000).map { chooseGen.next() }
      val actualUniqueValues = allGenValues.toSet()
      allGenValues.forEach { actualCountMap[it] = actualCountMap[it]!! + 1 }
      val actualCountTotal = actualCountMap.values.sum()
      val actualRatios = actualCountMap.values.map { it.toDouble() / actualCountTotal }

      actualUniqueValues shouldBe expectedUniqueValues

      (actualRatios.indices).forEach {
         actualRatios[it] shouldBe (expectedRatios[it] plusOrMinus 0.02)
      }
    }
  }

  test("should not accept negative weights") {
    shouldThrow<IllegalArgumentException> { Gen.choose(-1 to 'A', 1 to 'B') }
  }

  test("should not accept all zero weights") {
    shouldThrow<IllegalArgumentException> { Gen.choose(0 to 'A', 0 to 'B') }
  }
})
