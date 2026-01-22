package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveLength

class StringSpecTest : StringSpec() {

   init {

      "strings.size should return size of string" {
         "hello".length shouldBe 5
         "hello" should haveLength(5)
      }

      "strings should support config".config(enabled = true) {
         "hello".length shouldBe 5
      }

      "multiple invocations root test is allowed with config".config(invocations = 3) {
         1 + 1 shouldBe 2
      }

      "an ignored string test".config(enabled = false) {

      }

      val testName = "BCN_Epileptic.X"
      testName {
      }
   }
}

