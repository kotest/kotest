package com.sksamuel.kotest.specs.stringspec

import io.kotest.matchers.string.haveLength
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should

class StringSpecTest : StringSpec() {

   init {

      "strings.size should return size of string" {
         "hello".length shouldBe 5
         "hello" should haveLength(5)
      }

      "strings should support config".config(enabled = true) {
         "hello".length shouldBe 5
      }

      "an ignored string test".config(enabled = false) {

      }
   }
}

