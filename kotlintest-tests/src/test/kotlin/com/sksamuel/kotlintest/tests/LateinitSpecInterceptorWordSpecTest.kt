package com.sksamuel.kotlintest.tests

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.Extension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  inner class Interceptor : SpecExtension {
    override fun intercept(description: Description, spec: Spec, process: () -> Unit) {
      this@LateinitSpecInterceptorWordSpecTest.string = "Hello"
      process()
    }
  }

  override fun extensions(): List<Extension> = listOf(Interceptor())

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}