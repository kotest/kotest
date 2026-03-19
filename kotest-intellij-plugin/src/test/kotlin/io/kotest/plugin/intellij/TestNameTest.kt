package io.kotest.plugin.intellij

import io.kotest.matchers.shouldBe
import junit.framework.TestCase

class TestNameTest : TestCase() {

   fun `test displayName should flatten multiline test name to single line`() {
      val testName = TestName(null, """
         this is a test
         that spans multiple
         lines
      """.trimIndent(), interpolated = false)

      testName.displayName() shouldBe "this is a test that spans multiple lines"
   }

   fun `test displayName should collapse multiple spaces to single space`() {
      val testName = TestName(null, "this    has     many    spaces", interpolated = false)

      testName.displayName() shouldBe "this has many spaces"
   }

   fun `test displayName should trim leading and trailing whitespace`() {
      val testName = TestName(null, "   trimmed name   ", interpolated = false)

      testName.displayName() shouldBe "trimmed name"
   }

   fun `test displayName should include prefix when provided`() {
      val testName = TestName("Given: ", "a condition", interpolated = false)

      testName.displayName() shouldBe "Given: a condition"
   }

   fun `test displayName should handle tabs and newlines`() {
      val testName = TestName(null, "test\twith\ttabs\nand\nnewlines", interpolated = false)

      testName.displayName() shouldBe "test with tabs and newlines"
   }

   fun `test displayName should handle empty lines in multiline string`() {
      val testName = TestName(null, """
         first line

         third line
      """.trimIndent(), interpolated = false)

      testName.displayName() shouldBe "first line third line"
   }

   fun `test displayName with prefix and multiline name`() {
      val testName = TestName("When: ", """
         something
         happens
      """.trimIndent(), interpolated = false)

      testName.displayName() shouldBe "When: something happens"
   }

   fun `test displayName should preserve single spaces between words`() {
      val testName = TestName(null, "normal test name", interpolated = false)

      testName.displayName() shouldBe "normal test name"
   }
}
