package com.sksamuel.kotlintest.extensions

import io.kotlintest.Spec
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

  private lateinit var string: String

  inner class Interceptor : SpecExtension {
    override suspend fun intercept(spec: Spec, process: suspend () -> Unit) {
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