package com.sksamuel.kotest.extensions

import io.kotest.Spec
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.SpecLevelExtension
import io.kotest.shouldBe
import io.kotest.specs.WordSpec

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