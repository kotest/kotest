package com.sksamuel.kotest.engine.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit.platform.gradle.NestedTestsArgParser
import io.kotest.runner.junit.platform.gradle.NestedTestArg
import io.kotest.matchers.shouldBe

class NestedGradleTestsArgParserTest : FunSpec() {
   init {

      test("if only a package is specified should return null") {
         NestedTestsArgParser.parse("\\Qio.kotest\\E") shouldBe null
      }

      test("if only a class is specified should return null") {
         NestedTestsArgParser.parse("\\Qio.kotest.SomeTest\\E") shouldBe null
      }

      test("if only a class root test is specified should return null") {
         NestedTestsArgParser.parse("\\Qio.kotest.SomeTest.test\\E") shouldBe null
      }

      test("support nested tests") {
         NestedTestsArgParser.parse("\\Qio.kotest.SomeTest.test__--__nested\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("should support not starting with \\Q") {
         NestedTestsArgParser.parse("io.kotest.SomeTest.test__--__nested\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("should support not ending with \\E") {
         NestedTestsArgParser.parse("\\Qio.kotest.SomeTest.test__--__nested") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("support spaces in test names") {
         NestedTestsArgParser.parse("\\Qio.kotest.SomeTest.some method name__--__more names\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("some method name", "more names"))
      }

      test("support wildcards in test names") {
         NestedTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.Test.some method name__--__with a \\E.*\\Q wildcard\\E") shouldBe
            NestedTestArg("com.sksamuel.kotest.engine.gradle", "Test", listOf("some method name","with a * wildcard"))
      }
   }
}
