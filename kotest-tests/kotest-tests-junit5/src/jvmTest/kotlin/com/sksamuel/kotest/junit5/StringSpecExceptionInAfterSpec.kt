package com.sksamuel.kotest.junit5

import io.kotest.core.SpecClass
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

class StringSpecExceptionInAfterSpec : StringSpec() {

  init {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }

  }

  override fun afterSpec(spec: SpecClass) {
    throw RuntimeException("splatt!!")
  }

}
