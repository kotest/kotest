package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FocusTest : StringSpec() {

  private var focus = false

  override fun afterSpec(spec: Spec) {
    focus shouldBe true
  }

  init {
    "this should be ignored" {
      throw RuntimeException("boom")
    }

    "f:this is not ignored as it is focused" {
      focus = true
    }

    "this should be ignored too!" {
      throw RuntimeException("boom")
    }
  }
}
