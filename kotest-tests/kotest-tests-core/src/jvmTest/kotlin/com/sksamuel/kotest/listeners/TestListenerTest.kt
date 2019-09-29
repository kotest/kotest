package com.sksamuel.kotest.listeners

import io.kotest.Description
import io.kotest.Spec
import io.kotest.extensions.TestListener
import io.kotest.shouldBe
import io.kotest.specs.WordSpec

class TestListenerTest : WordSpec(), TestListener {

  var a: String? = null

  override fun beforeSpec(description: Description, spec: Spec) {
    a = "wibble"
  }

  override fun listeners(): List<TestListener> = listOf(this)

  init {
    "TestListener" should {
      "invoke before each spec" {
        a shouldBe "wibble"
      }
    }
  }
}