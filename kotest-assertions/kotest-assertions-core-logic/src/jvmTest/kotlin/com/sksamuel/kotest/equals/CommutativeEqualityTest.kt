package com.sksamuel.kotest.equals

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.equals.CommutativeEquality
import io.kotest.assertions.equals.Equality
import io.kotest.assertions.equals.EqualityResult
import io.kotest.assertions.equals.SimpleEqualityResult
import io.kotest.assertions.equals.SimpleEqualityResultDetail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

@EnabledIf(LinuxOnlyGithubCondition::class)
class CommutativeEqualityTest : StringSpec() {
   init {
      "verify true if commutative and both matches true" {
         val actual = CommutativeEquality<Int>(Equality.Companion.default()).verify(1, 1)
         actual.areEqual() shouldBe true
      }
      "verify false if commutative and both matches false" {
         val actual = CommutativeEquality<Int>(Equality.Companion.default()).verify(1, 2)
         actual.areEqual() shouldBe false
      }
      "verify false if non-commutative" {
         val nonCommutativeEquality = object : Equality<Int> {
            override fun name() = "actual should be 0"

            override fun verify(actual: Int, expected: Int): EqualityResult =
               SimpleEqualityResult(
                  equal = (actual == 0),
                  detailsValue = SimpleEqualityResultDetail(
                     explainFn = {
                        if (actual == 0) "actual == 0" else "actual != 0"
                     }
                  )
               )
         }
         val firstIsZero = CommutativeEquality(nonCommutativeEquality).verify(0, 1)
         val firstIsNotZero = CommutativeEquality(nonCommutativeEquality).verify(1, 0)
         assertSoftly {
            firstIsZero.areEqual() shouldBe false
            firstIsNotZero.areEqual() shouldBe false
            firstIsZero.details().explain() shouldStartWith "Non-commutative comparison"
            firstIsNotZero.details().explain() shouldStartWith "Non-commutative comparison"
         }
      }
   }
}
