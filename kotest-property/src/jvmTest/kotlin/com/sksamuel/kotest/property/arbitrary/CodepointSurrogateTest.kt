package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.asString

/**
 * Regression for a typo in `Codepoint.lowSurrogate`: it computed `value ushr 0x3ff` (shift
 * right by 1023, which Kotlin reduces to `mod 32 = 31` for Int) instead of `value and 0x3ff`.
 * That returned the sign bit instead of the low 10 bits, so the low surrogate of every
 * supplementary codepoint collapsed to `0xDC00`, corrupting any surrogate-pair output
 * (egyptianHieroglyphs(), emoji, etc.).
 *
 * The contract: `Codepoint(cp).asString()` round-trips through Java's UTF-16 encoding so
 * that the resulting string's first codepoint equals `cp`.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class CodepointSurrogateTest : FunSpec({

   test("supplementary codepoints round-trip through asString()") {
      // U+10000 - first supplementary codepoint
      Codepoint(0x10000).asString().codePointAt(0) shouldBe 0x10000

      // U+10001 - the low surrogate must encode the +1 in its low 10 bits
      Codepoint(0x10001).asString().codePointAt(0) shouldBe 0x10001

      // Egyptian hieroglyph in the middle of the range
      Codepoint(0x13042).asString().codePointAt(0) shouldBe 0x13042

      // U+1F600 (😀) - emoji territory
      Codepoint(0x1F600).asString().codePointAt(0) shouldBe 0x1F600

      // U+10FFFF - last valid Unicode codepoint
      Codepoint(0x10FFFF).asString().codePointAt(0) shouldBe 0x10FFFF
   }

   test("supplementary codepoints encode to exactly two chars") {
      Codepoint(0x10000).asString().length shouldBe 2
      Codepoint(0x10001).asString().length shouldBe 2
      Codepoint(0x1F600).asString().length shouldBe 2
   }

   test("BMP codepoints still encode as a single char") {
      Codepoint('a'.code).asString() shouldBe "a"
      Codepoint('Z'.code).asString() shouldBe "Z"
      Codepoint(0x4E2D).asString().codePointAt(0) shouldBe 0x4E2D
   }
})
