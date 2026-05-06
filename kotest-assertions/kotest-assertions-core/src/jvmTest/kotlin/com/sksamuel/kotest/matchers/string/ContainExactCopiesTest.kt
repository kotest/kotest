package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.shouldContainExactCopies
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContainExactCopies

class ContainExactCopiesTest : WordSpec() {
   init {
      "shouldContainExactCopies" should {
         "pass if contains exact count" {
            "Mayday".shouldContainExactCopies("ay", copies = 2, allowOverlaps = false)
            "121212".shouldContainExactCopies("1212", copies = 2, allowOverlaps = true)
            "12121212".shouldContainExactCopies("1212", copies = 2, allowOverlaps = false)
         }
         "fail if the count does not match" {
            shouldThrow<AssertionError> {
               "Mayday".shouldContainExactCopies("ay", copies = 1, allowOverlaps = false)
            }.message.shouldContainInOrder(
               "String should contain 1 copies of element \"ay\"",
               "but contained 2 copies at index(es) [1, 4]"
            )
            shouldThrow<AssertionError> {
               "Mayday".shouldContainExactCopies("ay", copies = 3, allowOverlaps = false)
            }.message.shouldContainInOrder(
               "String should contain 3 copies of element \"ay\"",
               "but contained 2 copies at index(es) [1, 4]"
            )
            shouldThrow<AssertionError> {
               "121212".shouldContainExactCopies("1212", copies = 2, allowOverlaps = false)
            }.message.shouldContainInOrder(
               "String should contain 2 copies of element \"1212\"",
               "but contained 1 copies at index(es) [0]"
            )
         }
      }
      "shouldNotContainExactCopies" should {
         "fail if the count matches" {
            shouldThrow<AssertionError> {
               "Mayday".shouldNotContainExactCopies("ay", copies = 2, allowOverlaps = false)
            }.message.shouldContainInOrder(
               "String should not contain 2 copies of element \"ay\", but it did at index(es):[1, 4]"
            )
            shouldThrow<AssertionError> {
               "121212".shouldNotContainExactCopies("1212", copies = 2, allowOverlaps = true)
            }.message.shouldContainInOrder(
               "String should not contain 2 copies of element \"1212\", but it did at index(es):[0, 2]"
            )
         }
         "pass if the count does not match" {
            "Mayday".shouldNotContainExactCopies("ay", copies = 1, allowOverlaps = false)
            "Mayday".shouldNotContainExactCopies("ay", copies = 3, allowOverlaps = false)
            "121212".shouldNotContainExactCopies("1212", copies = 2, allowOverlaps = false)
         }
      }
   }
}
