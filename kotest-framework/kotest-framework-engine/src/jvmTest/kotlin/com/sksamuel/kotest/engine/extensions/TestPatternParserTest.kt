package com.sksamuel.kotest.engine.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.TestPattern
import io.kotest.engine.extensions.TestPatternParser
import io.kotest.matchers.shouldBe

class TestPatternParserTest : FunSpec() {
   init {

      test("package only") {
         TestPatternParser.parse("io.kotest") shouldBe TestPattern("io.kotest", false, null, emptyList())
      }

      test("package with wildcard") {
         TestPatternParser.parse("io.kotest.*") shouldBe TestPattern("io.kotest", true, null, emptyList())
      }

      test("package and class") {
         TestPatternParser.parse("io.kotest.MySpec") shouldBe TestPattern("io.kotest", false, "MySpec", emptyList())
      }

      test("root test") {
         TestPatternParser.parse("io.kotest.MySpec.test") shouldBe
            TestPattern("io.kotest", false, "MySpec", listOf("test"))
      }

      test("root test with space") {
         TestPatternParser.parse("io.kotest.MySpec.test test") shouldBe
            TestPattern("io.kotest", false, "MySpec", listOf("test test"))
      }

      test("nested tests") {
         TestPatternParser.parse("io.kotest.MySpec.test -- nested") shouldBe
            TestPattern("io.kotest", false, "MySpec", listOf("test", "nested"))
      }

      test("spaces in nested test names") {
         TestPatternParser.parse("io.kotest.MySpec.some method name -- more names") shouldBe
            TestPattern("io.kotest", false, "MySpec", listOf("some method name", "more names"))
      }
   }
}
