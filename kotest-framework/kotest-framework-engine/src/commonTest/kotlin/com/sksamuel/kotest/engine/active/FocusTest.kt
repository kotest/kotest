package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FocusTest : WordSpec() {

  private var focus = false

  override fun afterSpec(spec: Spec) {
    focus shouldBe true
  }

  init {
    "this should be ignored" should {
      throw RuntimeException("boom")
    }

    "f:this is not ignored as it is focused" should {
      focus = true
    }

    "this should be ignored too!" should {
      throw RuntimeException("boom")
    }
  }
}
