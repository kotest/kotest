package com.sksamuel.kotest

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class AssertionErrorBuilderSoftlyTest: StringSpec() {
   init {
      "failSoftly works with assertSoftly" {
         val thrown = shouldThrowAny {
             assertSoftly {
                 AssertionErrorBuilder.failSoftly("Oops!")
                 2 * 2 shouldBe 5
             }
         }
         thrown.message shouldContain "Oops!"
         thrown.message shouldContain "expected:<5> but was:<4>"
      }
   }
}
