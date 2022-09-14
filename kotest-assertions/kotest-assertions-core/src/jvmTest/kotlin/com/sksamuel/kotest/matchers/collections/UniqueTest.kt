package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldNotBeUnique

class UniqueTest : FunSpec() {
   init {

      test("List.shouldBeUnique") {
         shouldThrowWithMessage<AssertionError>("Collection should be unique but contained duplicates of 1") {
            listOf(1, 1, 2).shouldBeUnique()
         }
         listOf(1, 2, 3).shouldBeUnique()
      }

      test("List.shouldNotBeUnique") {
         listOf(1, 1, 2).shouldNotBeUnique()
         shouldThrowWithMessage<AssertionError>("Collection should contain at least one duplicate element") {
            listOf(1, 2, 3).shouldNotBeUnique()
         }
      }

      test("Array.shouldBeUnique") {
         shouldThrowWithMessage<AssertionError>("Collection should be unique but contained duplicates of 1") {
            arrayOf(1, 1, 2).shouldBeUnique()
         }
         arrayOf(1, 2, 3).shouldBeUnique()
      }

      test("Array.shouldNotBeUnique") {
         arrayOf(1, 1, 2).shouldNotBeUnique()
         shouldThrowWithMessage<AssertionError>("Collection should contain at least one duplicate element") {
            arrayOf(1, 2, 3).shouldNotBeUnique()
         }
      }

      test("List.shouldBeUnique with comparator") {
         shouldThrowWithMessage<AssertionError>("Collection should be unique but contained duplicates of 1") {
            listOf(1, 1, 2).shouldBeUnique(compareBy { it })
         }
         listOf(1, 1, 1).shouldBeUnique(object : Comparator<Int> {
            override fun compare(p0: Int?, p1: Int?): Int {
               return 1
            }
         })
      }

      test("Array.shouldBeUnique with comparator") {
         shouldThrowWithMessage<AssertionError>("Collection should be unique but contained duplicates of 1") {
            arrayOf(1, 1, 2).shouldBeUnique(compareBy { it })
         }
         arrayOf(1, 1, 1).shouldBeUnique(object : Comparator<Int> {
            override fun compare(p0: Int?, p1: Int?): Int {
               return 1
            }
         })
      }
   }
}
