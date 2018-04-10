package com.sksamuel.kotlintest.tests

import io.kotlintest.extensions.Extension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class LateinitSpecInterceptorStringSpecTest : StringSpec() {

  private lateinit var string: String

  inner class Interceptor : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      this@LateinitSpecInterceptorStringSpecTest.string = "Hello"
      process()
    }
  }

  override fun extensions(): List<Extension> = listOf(Interceptor())

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}