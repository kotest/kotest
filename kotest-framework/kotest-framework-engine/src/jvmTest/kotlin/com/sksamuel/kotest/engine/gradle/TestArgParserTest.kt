package com.sksamuel.kotest.engine.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.gradle.TestFilter
import io.kotest.engine.gradle.TestFilterParser
import io.kotest.matchers.shouldBe

class TestArgParserTest : FunSpec() {
   init {

      test("support packages") {
         TestFilterParser.parse("\\Qkotest.io.kotest\\E") shouldBe TestFilter.Package("io.kotest")
      }

      test("support classes") {
         TestFilterParser.parse("\\Qkotest.io.kotest.SomeTest\\E") shouldBe TestFilter.Class("io.kotest.SomeTest")
      }

      test("support root tests") {
         TestFilterParser.parse("kotest.io.kotest.SomeTest.test\\E") shouldBe
            TestFilter.Test("io.kotest.SomeTest", listOf("test"))
      }

      test("support nested tests") {
         TestFilterParser.parse("\\Qkotest.io.kotest.SomeTest.test__context__nested\\E") shouldBe
            TestFilter.Test("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("support spaces in test names") {
         TestFilterParser.parse("\\Qkotest.io.kotest.SomeTest.some method name\\E") shouldBe
            TestFilter.Test("io.kotest.SomeTest", listOf("some method name"))
      }

      test("support wildcards in test names") {
         TestFilterParser.parse("\\Qkotest.com.sksamuel.kotest.engine.gradle.TestArgParserTest.some method name \\E.*\\Q with a wildcard\\E") shouldBe
            TestFilter.Test("com.sksamuel.kotest.engine.gradle.TestArgParserTest", listOf("some method name * with a wildcard"))
      }

      test("support wildcards in nested test names") {
         TestFilterParser.parse("\\Qkotest.com.sksamuel.kotest.engine.gradle.TestArgParserTest.some context__context__some method name \\E.*\\Q with a wildcard\\E") shouldBe
            TestFilter.Test("com.sksamuel.kotest.engine.gradle.TestArgParserTest", listOf("some context", "some method name * with a wildcard"))
      }
   }
}
