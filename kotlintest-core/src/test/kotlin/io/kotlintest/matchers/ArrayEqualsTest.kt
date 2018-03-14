package io.kotlintest.matchers

import io.kotlintest.specs.StringSpec

class ArrayEqualsTest : StringSpec() {
  init {
    "shouldBe should support int arrays" {
      val array = intArrayOf(1, 2, 3)
      array shouldBe intArrayOf(1, 2, 3)
    }
    "shouldBe should support int arrays" {
      val array = longArrayOf(1L, 2L, 3L)
      array shouldBe longArrayOf(1L, 2L, 3L)
    }
    "shouldBe should support double arrays" {
      val array = doubleArrayOf(1.0, 2.0, 3.0)
      array shouldBe doubleArrayOf(1.0, 2.0, 3.0)
    }
    "shouldBe should support boolean arrays" {
      val array = booleanArrayOf(true, false)
      array shouldBe booleanArrayOf(true, false)
    }
    "shouldBe should support generic arrays" {
      val array = arrayOf("hello", "welcome")
      array shouldBe arrayOf("hello", "welcome")
    }
  }
}
