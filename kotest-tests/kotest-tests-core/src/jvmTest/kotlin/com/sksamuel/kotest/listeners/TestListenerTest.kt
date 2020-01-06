package com.sksamuel.kotest.listeners

import io.kotest.core.Description
import io.kotest.SpecClass
import io.kotest.extensions.TestListener
import io.kotest.shouldBe
import io.kotest.specs.WordSpec

class TestListenerTest : WordSpec(), TestListener {

  var a: String? = null

  override fun beforeSpec(description: Description, spec: SpecClass) {
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
