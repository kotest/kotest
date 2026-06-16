package com.sksamuel.kotest.matchers

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.AssertionError

class AssertSoftlyFailureTest : StringSpec() {
   init {
      "a test which will throw exception from assertSoftly block"{
         shouldThrowAny {
            assertSoftly {
               throw Exception()
            }
         }
      }

      "a test which does not uses assertSoftly so here we should get assertion error" {
         shouldThrow<AssertionError> {
            1 shouldBe 0
         }
      }
   }
}
