package com.sksamuel.kotest.specs.stringspec

import io.kotest.matchers.string.haveLength
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec

class StringSpecConstructorTest : StringSpec({

  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})
