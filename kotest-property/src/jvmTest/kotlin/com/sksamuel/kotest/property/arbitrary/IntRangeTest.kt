package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.intRange
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class IntRangeTest : DescribeSpec({
   describe("Arb.intRange should") {
      it("should generate range in given domain") {
         forAll(10, Arb.intRange(1..10)) {
            it.first >= 1 && it.last <= 10
         }
      }

      describe("edge cases") {
         it("include empty range in edge cases") {
            Arb.intRange(0..10).edgecases() shouldContain IntRange.EMPTY
         }
      }
   }
})
