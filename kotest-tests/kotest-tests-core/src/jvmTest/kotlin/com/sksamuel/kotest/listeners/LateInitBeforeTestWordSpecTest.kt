package com.sksamuel.kotest.listeners

import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase

class LateInitBeforeTestWordSpecTest : WordSpec() {

   private lateinit var string: String

   override fun beforeTest(testCase: TestCase) {
      string = "Hello"
   }

   init {
      "setting a late init var" should {
         "be supported by word spec" {
            string shouldBe "Hello"
         }
      }
   }
}
