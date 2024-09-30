package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ArrayShouldBeTest : StringSpec() {
  init {
    "shouldBe should support boolean arrays" {
      val array: Any = booleanArrayOf(true, false)
      array shouldBe booleanArrayOf(true, false)
    }
    "shouldBe should support byte arrays" {
      val array: Any = byteArrayOf(1, 2, 3)
      array shouldBe byteArrayOf(1, 2, 3)
    }
    "shouldBe should support short arrays" {
      val array: Any = shortArrayOf(1, 2, 3)
      array shouldBe shortArrayOf(1, 2, 3)
    }
    "shouldBe should support char arrays" {
      val array: Any = charArrayOf('a', 'b', 'c')
      array shouldBe charArrayOf('a', 'b', 'c')
    }
    "shouldBe should support int arrays" {
      val array: Any = intArrayOf(1, 2, 3)
      array shouldBe intArrayOf(1, 2, 3)
    }
    "shouldBe should support long arrays" {
      val array: Any = longArrayOf(1L, 2L, 3L)
      array shouldBe longArrayOf(1L, 2L, 3L)
    }
    "shouldBe should support float arrays" {
      val array: Any = floatArrayOf(1f, 2f, 3f)
      array shouldBe floatArrayOf(1f, 2f, 3f)
    }
    "shouldBe should support double arrays" {
      val array: Any = doubleArrayOf(1.0, 2.0, 3.0)
      array shouldBe doubleArrayOf(1.0, 2.0, 3.0)
    }
    "shouldBe should support generic arrays" {
      val array: Any = arrayOf("hello", "welcome")
      array shouldBe arrayOf("hello", "welcome")

      val nulls: Any = arrayOfNulls<Any>(3)
      nulls shouldBe arrayOf<String?>(null, null, null)
    }
    "shouldBe should support nullable boolean arrays" {
      val someBooleans: Array<Boolean?> = arrayOf(false, true, false, null)
      someBooleans shouldBe arrayOf(false, true, false, null)
    }
  }
}
