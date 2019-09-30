package com.sksamuel.kotest.extensions

import io.kotest.Spec
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.SpecLevelExtension
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class LateinitSpecInterceptorStringSpecTest : StringSpec() {

  private lateinit var string: String

  inner class Interceptor : SpecExtension {
    override suspend fun intercept(spec: Spec, process: suspend () -> Unit) {
      this@LateinitSpecInterceptorStringSpecTest.string = "Hello"
      process()
    }
  }

  override fun extensions(): List<SpecLevelExtension> = listOf(Interceptor())

  init {
    "Hello should equal to Hello" {
      string shouldBe "Hello"
    }
  }
}
