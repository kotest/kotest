package com.sksamuel.kotest.engine.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.gradle.TestArg
import io.kotest.engine.gradle.TestArgParser
import io.kotest.matchers.shouldBe

class TestArgParserTest : FunSpec() {
   init {

      test("support packages") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest") shouldBe TestArg.Package("io.kotest")
      }

      test("support classes") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest.SomeTest") shouldBe TestArg.Class("io.kotest.SomeTest")
      }

      test("support root tests") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest.SomeTest.test") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("test"))
      }

      test("support nested tests") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest.SomeTest.test__context__nested") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("test", "nested"))
      }

      test("support dots in test names") {
         TestArgParser.parse("kotest_intellij_plugin.io.kotest.SomeTest.test.a__context__nested") shouldBe
            TestArg.Test("io.kotest.SomeTest", listOf("test.a", "nested"))
      }
   }
}
