package io.kotlintest.specs

import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe

class StringSpecTest : StringSpec() {

  init {

    "strings.size should return size of string" {
      "hello".length shouldBe 5
      "hello" should haveLength(5)
    }

    "strings should support config" {
      "hello".length shouldBe 5
    }.config(invocations = 5)
  }
}

class StringSpecConstructorTest : StringSpec({

  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})

class StringSpecParenthesisTest : StringSpec() {
  init {
    "parenthesis (here) " {
    }
  }
}