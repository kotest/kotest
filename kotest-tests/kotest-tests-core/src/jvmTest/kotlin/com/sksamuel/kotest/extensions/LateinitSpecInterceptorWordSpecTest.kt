package com.sksamuel.kotest.extensions

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.SpecLevelExtension
import io.kotest.core.spec.style.WordSpec
import io.kotest.shouldBe

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

   private lateinit var string: String

   inner class Interceptor : SpecExtension {
      override suspend fun intercept(spec: SpecConfiguration, process: suspend () -> Unit) {
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
