package com.sksamuel.kotest.matchers.ranges

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.Range
import io.kotest.matchers.ranges.closedClosed
import io.kotest.matchers.ranges.openClosed
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

class ClosedBeWithinClosedTest : WordSpec() {
   init {
      "should" should {
         "work" {
            forAll(
               Arb.int(1..5), Arb.int(1..5), Arb.int(1..5), Arb.int(1..5)
            ) { rangeStart, rangeLength, otherStart, otherLength ->
               val rangeEnd = rangeStart + rangeLength
               val otherEnd = otherStart + otherLength
               Range.closedClosed(rangeStart, rangeEnd).contains(
                  Range.openClosed(otherStart, otherEnd)
               ) == (rangeStart <= otherStart && otherEnd <= rangeEnd)
            }
         }
      }
   }
}
