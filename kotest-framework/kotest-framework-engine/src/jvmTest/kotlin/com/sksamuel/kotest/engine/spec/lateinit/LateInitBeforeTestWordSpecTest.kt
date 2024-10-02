package com.sksamuel.kotest.engine.spec.lateinit

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class LateInitBeforeTestWordSpecTest : WordSpec() {

   private lateinit var string: String

   override suspend fun beforeTest(testCase: TestCase) {
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
