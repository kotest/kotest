package com.sksamuel.kotest.inspectors

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withSystemProperty
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

class InspectorSizeTests : FunSpec({

   test("should error with large failure count #938") {
      shouldFail {
         List(10_000) { it }.forAll {
            it shouldBe -1
         }
      }
   }

   test("passed results are truncated when passed list length is over 10") {
      shouldThrowAny {
         (1..13).toList().forAll {
            it.shouldBeLessThanOrEqual(12)
         }
      }.message shouldBe "12 elements passed but expected 13\n" +
         "\n" +
         "The following elements passed:\n" +
         "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n" +
         "... and 2 more passed elements\n" +
         "\n" +
         "The following elements failed:\n" +
         "13 => 13 should be <= 12"
   }

   test("failed results are truncated when failed array size is over 10") {
      shouldThrowAny {
         arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forAll {
            if (System.currentTimeMillis() > 0) throw NullPointerException()
         }
      }.message shouldBe "0 elements passed but expected 12\n" +
         "\n" +
         "The following elements passed:\n" +
         "--none--\n" +
         "\n" +
         "The following elements failed:\n" +
         "1 => java.lang.NullPointerException\n" +
         "2 => java.lang.NullPointerException\n" +
         "3 => java.lang.NullPointerException\n" +
         "4 => java.lang.NullPointerException\n" +
         "5 => java.lang.NullPointerException\n" +
         "6 => java.lang.NullPointerException\n" +
         "7 => java.lang.NullPointerException\n" +
         "8 => java.lang.NullPointerException\n" +
         "9 => java.lang.NullPointerException\n" +
         "10 => java.lang.NullPointerException\n" +
         "... and 2 more failed elements"
   }

   test("max results is controllable by sys prop") {
      withSystemProperty("kotest.assertions.output.max", "3") {
         shouldThrowAny {
            arrayOf(1, 2, 3, 4, 5).forAll {
               if (System.currentTimeMillis() > 0) throw NullPointerException()
            }
         }.message shouldBe "0 elements passed but expected 5\n" +
            "\n" +
            "The following elements passed:\n" +
            "--none--\n" +
            "\n" +
            "The following elements failed:\n" +
            "1 => java.lang.NullPointerException\n" +
            "2 => java.lang.NullPointerException\n" +
            "3 => java.lang.NullPointerException\n" +
            "... and 2 more failed elements"
      }
   }

})
