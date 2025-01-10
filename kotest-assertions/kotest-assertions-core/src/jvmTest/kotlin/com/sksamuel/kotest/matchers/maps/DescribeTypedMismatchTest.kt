package com.sksamuel.kotest.matchers.maps

import io.kotest.assertions.describeTypedMismatch
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class DescribeTypedMismatchTest: StringSpec() {
   init {
       "empty string for null expected" {
          describeTypedMismatch(
             expected = null,
             actual = 1.5
          ) shouldBe ""
       }
      "empty string for null actual" {
         describeTypedMismatch(
            expected = 1.5,
            actual = null
         ) shouldBe ""
      }
      "empty string for different print values" {
         describeTypedMismatch(
            expected = BigDecimal("1.6"),
            actual = 1.5
         ) shouldBe ""
      }
      "type mismatch for same print values" {
         describeTypedMismatch(
            expected = BigDecimal("1.5"),
            actual = 1.5
         ) shouldBe "\nExpected type java.math.BigDecimal, but was kotlin.Double"
      }
   }
}
