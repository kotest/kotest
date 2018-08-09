package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.matchers.haveLength
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

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

