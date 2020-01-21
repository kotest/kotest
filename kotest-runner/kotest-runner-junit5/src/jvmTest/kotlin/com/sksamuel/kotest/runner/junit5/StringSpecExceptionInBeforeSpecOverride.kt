package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.Spec
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

internal class StringSpecExceptionInBeforeSpecOverride : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }
   }

   override fun beforeSpec(spec: Spec) {
      throw RuntimeException("zopp!!")
   }

}

internal class StringSpecExceptionInBeforeSpecFunction : StringSpec() {
   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      beforeSpec {
         throw RuntimeException("zopp!!")
      }
   }
}
