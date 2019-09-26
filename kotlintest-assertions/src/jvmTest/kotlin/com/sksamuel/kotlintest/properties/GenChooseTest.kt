package com.sksamuel.kotlintest.properties

import io.kotlintest.data.forall
import io.kotlintest.properties.Gen
import io.kotlintest.properties.choose
import io.kotlintest.properties.next
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.row

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
})