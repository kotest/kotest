package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldBeInOpenEndRange
import io.kotest.matchers.ranges.shouldNotBeInOpenEndRange
import io.kotest.matchers.shouldBe

class ShouldBeInOpenEndRangeTest : WordSpec() {
   private val openEndRange = 0.1 ..< 0.3

   init {
      "shouldBeInOpenEndRange" should {
         "fail before left end of open end range" {
            shouldThrowAny {
               0.0 shouldBeInOpenEndRange openEndRange
            }.message shouldBe "Range should contain 0.0, but doesn't. Possible values: 0.1..<0.3"
         }

         "succeed on left end of open end range" {
            0.1 shouldBeInOpenEndRange openEndRange
         }

         "succeed inside open end range" {
            0.2 shouldBeInOpenEndRange openEndRange
         }

         "fail on right end of open end range" {
            shouldThrowAny {
               0.3 shouldBeInOpenEndRange openEndRange
            }.message shouldBe "Range should contain 0.3, but doesn't. Possible values: 0.1..<0.3"
         }

         "fail after right end of open end range" {
            shouldThrowAny {
               0.4 shouldBeInOpenEndRange openEndRange
            }.message shouldBe "Range should contain 0.4, but doesn't. Possible values: 0.1..<0.3"
         }
      }

      "shouldNotBeInOpenEndRange" should {
         "succeed before left end of open end range" {
            0.0 shouldNotBeInOpenEndRange openEndRange
         }

         "fail on left end of open end range" {
            shouldThrowAny {
               0.1 shouldNotBeInOpenEndRange openEndRange
            }.message shouldBe "Range should not contain 0.1, but does. Forbidden values: 0.1..<0.3"
         }

         "fail inside open end range" {
            shouldThrowAny {
               0.2 shouldNotBeInOpenEndRange openEndRange
            }.message shouldBe "Range should not contain 0.2, but does. Forbidden values: 0.1..<0.3"
         }

         "succeed on right end of open end range" {
            0.3 shouldNotBeInOpenEndRange openEndRange
         }

         "succeed after right end of open end range" {
            0.4 shouldNotBeInOpenEndRange openEndRange
         }
      }
   }
}
