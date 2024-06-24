package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.describeListsMismatch
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty

class DescribeListsMismatchTest: StringSpec() {
   init {
       "return empty string for match" {
          val list = listOf(1, 2, 3)
          describeListsMismatch(list, list).shouldBeEmpty()
       }

      "describe mismatch without nulls" {
         describeListsMismatch(listOf(1, 2, 3), listOf(2, 3, 1)) shouldBe
            """

Mismatch:
expected[0] = 1

Match:
expected[1] == actual[0]: 2
expected[2] == actual[1]: 3

Mismatch:
actual[2] = 1

            """.trimIndent()
      }

      "describe mismatch with nulls" {
         describeListsMismatch(listOf(1, 2, null), listOf(2, null, 1)) shouldBe
            """

Mismatch:
expected[0] = 1

Match:
expected[1] == actual[0]: 2
expected[2] == actual[1]: <null>

Mismatch:
actual[2] = 1

            """.trimIndent()
      }
   }
}
