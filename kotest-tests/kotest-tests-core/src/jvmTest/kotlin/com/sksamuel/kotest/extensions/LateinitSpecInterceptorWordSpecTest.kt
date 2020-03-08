package com.sksamuel.kotest.extensions

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class LateinitSpecInterceptorWordSpecTest : WordSpec() {

   private lateinit var string: String

   inner class Interceptor : SpecExtension {
      override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
         this@LateinitSpecInterceptorWordSpecTest.string = "Hello"
         process()
      }
   }

   override fun extensions() = listOf(Interceptor())

   init {
      "setting a late init var" should {
         "be supported by word spec" {
            string shouldBe "Hello"
         }
      }
   }
}
