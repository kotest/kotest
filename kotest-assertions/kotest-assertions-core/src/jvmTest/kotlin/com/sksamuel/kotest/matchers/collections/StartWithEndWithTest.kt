package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.endWith
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldNotEndWith
import io.kotest.matchers.collections.shouldNotStartWith
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.collections.startWith
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage

class StartWithEndWithTest : WordSpec() {
   init {
      "startWith" should {
         "test that a list starts with the given collection" {
            val col = listOf(1, 2, 3, 4, 5)
            col.shouldStartWith(listOf(1))
            col.shouldStartWith(listOf(1, 2))
            col.shouldNotStartWith(listOf(2, 3))
            col.shouldNotStartWith(listOf(4, 5))
            col.shouldNotStartWith(listOf(1, 3))
         }
         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf(1L, 2L) should startWith(listOf(1L, 3L))
            }.shouldHaveMessage("""
               |List should start with [1L, 3L] but was [1L, 2L]
               |Mismatched elements: value[1] != 3L
            """.trimMargin())
         }
         "print errors unambiguously when the actual value is empty"  {
            shouldThrow<AssertionError> {
               emptyList<Long>() should startWith(listOf(1L, 3L))
            }.shouldHaveMessage("""
               |List should start with [1L, 3L] but was []
               |Actual collection is shorter than expected slice
               """.trimMargin())
         }
      }

      "endWith" should {
         "test that a list ends with the given collection" {
            val col = listOf(1, 2, 3, 4, 5)
            col.shouldEndWith(listOf(5))
            col.shouldEndWith(listOf(4, 5))
            col.shouldNotEndWith(listOf(2, 3))
            col.shouldNotEndWith(listOf(3, 5))
            col.shouldNotEndWith(listOf(1, 2))
         }
         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf(1L, 2L, 3L, 4L) should endWith(listOf(1L, 3L))
            }.shouldHaveMessage("""
               |List should end with [1L, 3L] but was [3L, 4L]
               |Mismatched elements: value[2] != 1L, value[3] != 3L
               """.trimMargin())
         }
         "print errors unambiguously when the actual value is empty"  {
            shouldThrow<AssertionError> {
               emptyList<Long>() should endWith(listOf(1L, 3L))
            }.shouldHaveMessage("""
               |List should end with [1L, 3L] but was []
               |Actual collection is shorter than expected slice
               """.trimMargin())
         }
      }
   }
}
