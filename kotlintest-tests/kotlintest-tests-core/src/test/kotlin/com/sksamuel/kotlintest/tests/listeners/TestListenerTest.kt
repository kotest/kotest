package com.sksamuel.kotlintest.tests.listeners

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

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