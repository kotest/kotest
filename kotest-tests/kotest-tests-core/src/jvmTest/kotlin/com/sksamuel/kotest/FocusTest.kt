package com.sksamuel.kotest

import io.kotest.Spec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.specs.WordSpec

class FocusTest : WordSpec() {

  private var focus = false

  override fun afterSpec(spec: Spec) {
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
