package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.Spec
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

internal class StringSpecExceptionInAfterSpec : StringSpec() {

  init {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }

  }

  override fun afterSpec(spec: Spec) {
    throw RuntimeException("splatt!!")
  }

}

internal class StringSpecExceptionInAfterSpecFunction : StringSpec() {

   init {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      afterSpec {
         throw RuntimeException("splatt!!")
      }
   }
}
