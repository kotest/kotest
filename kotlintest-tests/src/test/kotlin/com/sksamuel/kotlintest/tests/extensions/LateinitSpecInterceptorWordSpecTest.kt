package com.sksamuel.kotlintest.tests.extensions

import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  inner class Interceptor : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      this@LateinitSpecInterceptorWordSpecTest.string = "Hello"
      process()
    }
  }

  override fun extensions(): List<SpecLevelExtension> = listOf(Interceptor())

  init {
    "setting a late init var" should {
      "be supported by word spec" {
        string shouldBe "Hello"
      }
    }
  }
}