package io.kotest.plugin.intellij.run.gradle

import io.kotest.matchers.shouldBe
import org.junit.Test

class TestNameNormalizerTest {

   // normalize

   @Test
   fun `normalize leaves plain names unchanged`() {
      TestNameNormalizer.normalize("hello world") shouldBe "hello world"
   }

   @Test
   fun `normalize trims leading and trailing whitespace`() {
      TestNameNormalizer.normalize("  hello  ") shouldBe "hello"
      TestNameNormalizer.normalize("\thello\t") shouldBe "hello"
   }

   @Test
   fun `normalize replaces newline with space`() {
      TestNameNormalizer.normalize("hello\nworld") shouldBe "hello world"
   }

   @Test
   fun `normalize replaces carriage return with space`() {
      TestNameNormalizer.normalize("hello\rworld") shouldBe "hello world"
   }

   @Test
   fun `normalize replaces CRLF with space`() {
      TestNameNormalizer.normalize("hello\r\nworld") shouldBe "hello world"
   }

   @Test
   fun `normalize handles multiple newlines`() {
      TestNameNormalizer.normalize("a\nb\nc") shouldBe "a b c"
   }

   @Test
   fun `normalize trims after newline replacement`() {
      TestNameNormalizer.normalize("\nhello\n") shouldBe "hello"
   }

   // normalizeAndEscape

   @Test
   fun `normalizeAndEscape leaves names without special characters unchanged`() {
      TestNameNormalizer.normalizeAndEscape("hello world") shouldBe "hello world"
   }

   @Test
   fun `normalizeAndEscape escapes single quote`() {
      TestNameNormalizer.normalizeAndEscape("it's a test") shouldBe "it'\\''s a test"
   }

   @Test
   fun `normalizeAndEscape escapes multiple single quotes`() {
      TestNameNormalizer.normalizeAndEscape("it's 'special'") shouldBe "it'\\''s '\\''special'\\''"
   }


   @Test
   fun `normalizeAndEscape normalizes newlines before escaping`() {
      TestNameNormalizer.normalizeAndEscape("line1\nline2") shouldBe "line1 line2"
   }

   @Test
   fun `normalizeAndEscape trims whitespace before escaping`() {
      TestNameNormalizer.normalizeAndEscape("  test  ") shouldBe "test"
   }

   @Test
   fun `normalizeAndEscape normalizes and escapes combined`() {
      TestNameNormalizer.normalizeAndEscape("  it's\na test  ") shouldBe "it'\\''s a test"
   }
}
