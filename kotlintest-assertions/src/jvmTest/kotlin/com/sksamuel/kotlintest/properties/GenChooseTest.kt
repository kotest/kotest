package com.sksamuel.kotlintest.properties

import io.kotlintest.data.forall
import io.kotlintest.properties.Gen
import io.kotlintest.properties.choose
import io.kotlintest.properties.next
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.row

class GenChooseTest : FunSpec({
  test("should give ints in between min and max inclusive") {
    forall(
      row(-10 to -1, (-10..-1).toSet()),
      row(1 to 3, (1..3).toSet()),
      row(-100 to 100, (-100..100).toSet())
    ) { minMax, expectedValues ->
      val vmin = minMax.first
      val vmax = minMax.second
      val actualValues = (1..100000).map { Gen.choose(vmin, vmax).next() }.toSet()

      actualValues shouldBe expectedValues
    }
  }

  test("should give longs in between min and max inclusive") {
    forall(
      row(-10L to -1L, (-10L..-1L).toSet()),
      row(1L to 3L, (1L..3L).toSet()),
      row(-100L to 100L, (-100L..100L).toSet())
    ) { minMax, expectedValues ->
      val vmin = minMax.first
      val vmax = minMax.second
      val actualValues = (1..100000).map { Gen.choose(vmin, vmax).next() }.toSet()

      actualValues shouldBe expectedValues
    }
  }
})