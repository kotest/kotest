package com.sksamuel.kotest.engine.spec.style

import io.kotest.matchers.string.haveLength
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should

class StringSpecConstructorExampleTest : StringSpec({

  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})
