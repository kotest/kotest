package com.sksamuel.kotest.inspectors

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.common.nonConstantTrue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withSystemProperty
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
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
      }.message shouldBe """12 elements passed but expected 13

The following elements passed:
  [0] 1
  [1] 2
  [2] 3
  [3] 4
  [4] 5
  [5] 6
  [6] 7
  [7] 8
  [8] 9
  [9] 10
  ... and 2 more passed elements

The following elements failed:
  [12] 13 => 13 should be <= 12
"""
   }

   test("failed results are truncated when failed array size is over 10") {
      shouldThrowAny {
         arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forAll {
            if (nonConstantTrue()) throw NullPointerException()
         }
      }.message shouldBe """0 elements passed but expected 12

The following elements passed:
  --none--

The following elements failed:
  [0] 1 => java.lang.NullPointerException
  [1] 2 => java.lang.NullPointerException
  [2] 3 => java.lang.NullPointerException
  [3] 4 => java.lang.NullPointerException
  [4] 5 => java.lang.NullPointerException
  [5] 6 => java.lang.NullPointerException
  [6] 7 => java.lang.NullPointerException
  [7] 8 => java.lang.NullPointerException
  [8] 9 => java.lang.NullPointerException
  [9] 10 => java.lang.NullPointerException
  ... and 2 more failed elements
"""
   }

   test("max results is controllable by sys prop") {
      withSystemProperty("kotest.assertions.output.max", "3") {
         shouldThrowAny {
            arrayOf(1, 2, 3, 4, 5).forAll {
               if (nonConstantTrue()) throw NullPointerException()
            }
         }.message shouldBe """0 elements passed but expected 5

The following elements passed:
  --none--

The following elements failed:
  [0] 1 => java.lang.NullPointerException
  [1] 2 => java.lang.NullPointerException
  [2] 3 => java.lang.NullPointerException
  ... and 2 more failed elements
"""
      }
   }
})
