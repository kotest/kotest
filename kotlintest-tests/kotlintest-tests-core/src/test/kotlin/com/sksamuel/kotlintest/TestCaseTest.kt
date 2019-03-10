package com.sksamuel.kotlintest

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class TestCaseTest : FunSpec() {

  init {

    test("test case is focused should return true for top level f: test") {
      val test = TestCase.test(Description.spec("f: my test"), this@TestCaseTest) {}
      test.isFocused() shouldBe true
    }

    test("test case is focused should return false for top level test without f: prefix") {
      val test = TestCase.test(Description.spec("my test"), this@TestCaseTest) {}
      test.isFocused() shouldBe false
    }

    test("is bang should return true for tests with ! prefix") {
      val test = TestCase.test(Description.spec("!my test"), this@TestCaseTest) {}
      test.isBang() shouldBe true
    }

    test("is bang should return false for tests without ! prefix") {
      val test = TestCase.test(Description.spec("my test"), this@TestCaseTest) {}
      test.isBang() shouldBe false
    }
  }

}