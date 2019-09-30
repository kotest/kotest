package com.sksamuel.kotest

import io.kotest.Description
import io.kotest.TestCase
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class TestCasePrefixTest : FunSpec() {

  init {

    test("test case is focused should return true for top level f: test") {
      val test = TestCase.test(Description.spec("f: my test"), this@TestCasePrefixTest) {}
      test.isFocused() shouldBe true
    }

    test("test case is focused should return false for top level test without f: prefix") {
      val test = TestCase.test(Description.spec("my test"), this@TestCasePrefixTest) {}
      test.isFocused() shouldBe false
    }

    test("is bang should return true for tests with ! prefix") {
      val test = TestCase.test(Description.spec("!my test"), this@TestCasePrefixTest) {}
      test.isBang() shouldBe true
    }

    test("is bang should return false for tests without ! prefix") {
      val test = TestCase.test(Description.spec("my test"), this@TestCasePrefixTest) {}
      test.isBang() shouldBe false
    }
  }

}
