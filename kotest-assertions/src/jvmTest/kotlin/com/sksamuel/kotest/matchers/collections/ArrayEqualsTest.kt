package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ArrayEqualsTest : StringSpec() {
  init {
    "shouldBe should support int arrays" {
      val array: Any = intArrayOf(1, 2, 3)
      array shouldBe intArrayOf(1, 2, 3)
    }
    "shouldBe should support long arrays" {
      val array: Any = longArrayOf(1L, 2L, 3L)
      array shouldBe longArrayOf(1L, 2L, 3L)
    }
    "shouldBe should support double arrays" {
      val array: Any = doubleArrayOf(1.0, 2.0, 3.0)
      array shouldBe doubleArrayOf(1.0, 2.0, 3.0)
    }
    "shouldBe should support boolean arrays" {
      val array: Any = booleanArrayOf(true, false)
      array shouldBe booleanArrayOf(true, false)
    }
    "shouldBe should support generic arrays" {
      val array: Any = arrayOf("hello", "welcome")
      array shouldBe arrayOf("hello", "welcome")
    }
    "shouldBe should support nullable boolean arrays" {
      val someBooleans: Array<Boolean?> = arrayOf(false, true, false)
      someBooleans shouldBe arrayOf<Boolean?>(false, true, false)
    }
  }
}
