package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.matchers.ranges.shouldNotBeIn
import io.kotest.matchers.shouldBe

class ShouldBeInTest : WordSpec() {
   private val closedRange = 1..3
   private val openEndRange = 1 until 3

   init {
      "shouldBeIn" should {
         "fail before left end of closed range" {
            shouldThrowAny {
               0 shouldBeIn closedRange
            }.message shouldBe "Range should contain 0, but doesn't. Possible values: 1..3"
         }

         "fail before left end of open end range" {
            shouldThrowAny {
               0 shouldBeIn openEndRange
            }.message shouldBe "Range should contain 0, but doesn't. Possible values: 1..2"
         }

         "succeed on left end of closed range" {
            1 shouldBeIn closedRange
         }

         "succeed on left end of open end range" {
            1 shouldBeIn openEndRange
         }

         "succeed inside closed range" {
            2 shouldBeIn closedRange
         }

         "succeed inside open end range" {
            2 shouldBeIn openEndRange
         }

         "succeed on right end of closed range" {
            3 shouldBeIn closedRange
         }

         "fail on right end of open end range" {
            shouldThrowAny {
               3 shouldBeIn openEndRange
            }.message shouldBe "Range should contain 3, but doesn't. Possible values: 1..2"
         }

         "fail after right end of closed range" {
            shouldThrowAny {
               4 shouldBeIn closedRange
            }.message shouldBe "Range should contain 4, but doesn't. Possible values: 1..3"
         }

         "fail after right end of open end range" {
            shouldThrowAny {
               4 shouldBeIn openEndRange
            }.message shouldBe "Range should contain 4, but doesn't. Possible values: 1..2"
         }
      }

      "shouldNotBeIn" should {
         "succeed before left end of closed range" {
            0 shouldNotBeIn closedRange
         }

         "succeed before left end of open end range" {
            0 shouldNotBeIn openEndRange
         }

         "fail on left end of closed range" {
            shouldThrowAny {
               1 shouldNotBeIn closedRange
            }.message shouldBe "Range should not contain 1, but does. Forbidden values: 1..3"
         }

         "fail on left end of open end range" {
            shouldThrowAny {
               1 shouldNotBeIn openEndRange
            }.message shouldBe "Range should not contain 1, but does. Forbidden values: 1..2"
         }

         "fail inside closed range" {
            shouldThrowAny {
               2 shouldNotBeIn closedRange
            }.message shouldBe "Range should not contain 2, but does. Forbidden values: 1..3"
         }

         "fail inside open end range" {
            shouldThrowAny {
               2 shouldNotBeIn openEndRange
            }.message shouldBe "Range should not contain 2, but does. Forbidden values: 1..2"
         }

         "fail on right end of closed range" {
            shouldThrowAny {
               3 shouldNotBeIn closedRange
            }.message shouldBe "Range should not contain 3, but does. Forbidden values: 1..3"
         }

         "succeed on right end of open end range" {
            3 shouldNotBeIn openEndRange
         }

         "succeed after right end of closed range" {
            3 shouldNotBeIn openEndRange
         }

         "succeed after right end of open end range" {
            3 shouldNotBeIn openEndRange
         }
      }
   }
}
