package com.sksamuel.kotlintest

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestType
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class TestCaseTest : FunSpec() {

  init {

    test("test case is focused should return true for top level f: test") {
      val config = TestCaseConfig()
      val test = TestCase(Description.spec("f: my test"), this@TestCaseTest, {}, 1, TestType.Test, config)
      test.isFocused() shouldBe true
    }

    test("test case is focused should return false for top level test without f: prefix") {
      val config = TestCaseConfig()
      val test = TestCase(Description.spec("my test"), this@TestCaseTest, {}, 1, TestType.Test, config)
      test.isFocused() shouldBe false
    }

    test("is bang should return true for tests with ! prefix") {
      val config = TestCaseConfig()
      val test = TestCase(Description.spec("!my test"), this@TestCaseTest, {}, 1, TestType.Test, config)
      test.isBang() shouldBe true
    }

    test("is bang should return false for tests without ! prefix") {
      val config = TestCaseConfig()
      val test = TestCase(Description.spec("my test"), this@TestCaseTest, {}, 1, TestType.Test, config)
      test.isBang() shouldBe false
    }
  }

}