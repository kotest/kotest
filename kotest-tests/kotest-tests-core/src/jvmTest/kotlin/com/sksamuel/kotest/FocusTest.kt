package com.sksamuel.kotest

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeTrue

class FocusTest : WordSpec() {

  private var focus = false

  override fun afterSpec(spec: SpecConfiguration) {
    focus.shouldBeTrue()
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
