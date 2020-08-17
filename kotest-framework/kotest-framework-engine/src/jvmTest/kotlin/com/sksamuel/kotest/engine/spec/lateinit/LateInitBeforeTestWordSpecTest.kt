package com.sksamuel.kotest.engine.spec.lateinit

import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

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
