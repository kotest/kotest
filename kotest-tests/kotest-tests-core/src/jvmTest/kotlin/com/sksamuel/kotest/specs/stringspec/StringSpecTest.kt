package com.sksamuel.kotest.specs.stringspec

import io.kotest.matchers.string.haveLength
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

class StringSpecTest : StringSpec() {

   init {

      "strings.size should return size of string" {
         "hello".length shouldBe 5
         "hello" should haveLength(5)
      }

      "strings should support config".config(invocations = 5) {
         "hello".length shouldBe 5
      }
   }
}

