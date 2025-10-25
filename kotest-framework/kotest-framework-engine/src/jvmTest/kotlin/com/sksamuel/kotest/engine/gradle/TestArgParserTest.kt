package com.sksamuel.kotest.engine.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.gradle.TestArg
import io.kotest.engine.gradle.TestArgParser
import io.kotest.matchers.shouldBe

class TestArgParserTest : FunSpec() {
   init {

      test("support packages") {
         TestArgParser.parse("\\Qkotest_intellij_plugin.io.kotest\\E") shouldBe TestArg.Package("io.kotest")
      }

      test("support classes") {
         TestArgParser.parse("\\Qkotest_intellij_plugin.io.kotest.SomeTest\\E") shouldBe TestArg.Class("io.kotest.SomeTest")
      }

      test("support root tests") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest.SomeTest.test\\E") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("test"))
      }

      test("support nested tests") {
         TestArgParser.parse("\\Qkotest_intellij_plugin.io.kotest.SomeTest.test__context__nested\\E") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("support spaces in test names") {
         TestArgParser.parse("\\Qkotest_intellij_plugin.io.kotest.SomeTest.some method name\\E") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("some method name"))
      }

      test("f:support regex in in test names") {
         TestArgParser.parse("\\Qkotest_intellij_plugin.com.sksamuel.kotest.engine.gradle.TestArgParserTest.some method name \\E.*\\Q with a period\\E") shouldBe
            TestArg.Test("com.sksamuel.kotest.engine.gradle.TestArgParserTest", listOf("some method name . with a period"))
      }
   }
}
