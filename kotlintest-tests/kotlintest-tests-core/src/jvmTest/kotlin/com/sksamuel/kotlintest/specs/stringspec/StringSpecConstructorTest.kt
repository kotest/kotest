package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.matchers.string.haveLength
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractStringSpec

class StringSpecConstructorTest : AbstractStringSpec({

  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})