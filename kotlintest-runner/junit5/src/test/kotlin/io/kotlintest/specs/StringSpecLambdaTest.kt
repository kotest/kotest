package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.StringSpec
import io.kotlintest.shouldBe

class StringSpecLambdaTest : StringSpec({
  "strings.length should return size of string" {
    "hello".length shouldBe 5
  }
})