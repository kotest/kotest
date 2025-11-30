package com.sksamuel.kotest.engine.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.gradle.NestedGradleTestsArgParser
import io.kotest.engine.gradle.NestedTestArg
import io.kotest.matchers.shouldBe

class NestedGradleTestsArgParserTest : FunSpec() {
   init {

      test("if only a package is specified should return null") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest\\E") shouldBe null
      }

      test("if only a class is specified should return null") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest.SomeTest\\E") shouldBe null
      }

      test("if only a class root test is specified should return null") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest.SomeTest.test\\E") shouldBe null
      }

      test("support nested tests") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest.SomeTest.test__--__nested\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("should support not starting with \\Q") {
         NestedGradleTestsArgParser.parse("io.kotest.SomeTest.test__--__nested\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("should support not ending with \\E") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest.SomeTest.test__--__nested") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("test", "nested"))
      }

      test("support spaces in test names") {
         NestedGradleTestsArgParser.parse("\\Qio.kotest.SomeTest.some method name__--__more names\\E") shouldBe
            NestedTestArg("io.kotest", "SomeTest", listOf("some method name", "more names"))
      }

      test("support wildcards in test names") {
         NestedGradleTestsArgParser.parse("\\Qcom.sksamuel.kotest.engine.gradle.Test.some method name__--__with a \\E.*\\Q wildcard\\E") shouldBe
            NestedTestArg("com.sksamuel.kotest.engine.gradle", "Test", listOf("some method name","with a * wildcard"))
      }
   }
}
