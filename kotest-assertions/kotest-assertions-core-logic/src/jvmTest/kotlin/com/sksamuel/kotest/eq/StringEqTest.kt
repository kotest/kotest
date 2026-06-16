package com.sksamuel.kotest.eq

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.eq.StringEq
import io.kotest.assertions.shouldFailWithMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder

class StringEqTest : FunSpec({
   test("string eq should highlight line break diffs") {
      val result = StringEq.equals("foo\nbar\r", "\r\nfoo\nbar\r\n", EqContext()) as EqResult.Failure
      result.error().message shouldBe """
         |(contents match, but line-breaks differ; output has been escaped to show line-breaks)
         |expected:<\r\nfoo\nbar\r\n> but was:<foo\nbar\r>
         """.trimMargin()
   }

   test("StringEq shows type information when relevant") {
      shouldFailWithMessage("expected:java.lang.StringBuilder<foo> but was:kotlin.String<foo>") {
         "foo" shouldBe StringBuilder("foo")
      }
   }

   test("String comparison with same types dont include type information") {
      shouldFailWithMessage("expected:<bar> but was:<foo>") {
         "foo" shouldBe "bar"
      }
   }

   test("Find partial match for string") {
      val expected = "One quick brown fox jumps over the lazy cat"
      val value = "The quick brown fox jumps over the lazy dog"
      val actual = shouldThrow<AssertionError> { value shouldBe expected }.message
      actual.shouldContainInOrder(
         "Match[0]: part of slice with indexes [2..39] matched actual[2..39]",
         """Line[0] ="The quick brown fox jumps over the lazy dog"""",
         """Match[0]= --++++++++++++++++++++++++++++++++++++++---""",
         """expected:<One quick brown fox jumps over the lazy cat> but was:<The quick brown fox jumps over the lazy dog>"""
      )
   }

   test("should map windows when mapFileEndingsToUnix is set to true ") {
      AssertionsConfig.mapFileEndingsToUnix.withValue(true) {
         val mixedString = "Windows\r\nUnix\nOldMac\r"
         val unixString = "Windows\nUnix\nOldMac\n"
         mixedString shouldBe unixString
      }
   }

   // Regression test for #5944: when StringEq picks the large-string diff path it must surface
   // the per-chunk "[Change at line N] ..." output produced by `diffLargeString`, not the original
   // strings. Previously both branches of the `if/else` in `diff()` used the original strings, so
   // multi-line mismatches over `largeStringDiffMinSize` lines silently lost the diff output.
   // Gate to Linux GitHub CI: `useDiff` early-returns false when running inside IntelliJ
   // (sets idea.active or similar), so the diff-path is only reachable on plain JVM CI runs.
   test("StringEq.equals should surface diffLargeString chunk output for large multi-line mismatches").config(
      enabledIf = { System.getenv("CI") == "true" && System.getProperty("idea.active") == null }
   ) {
      val expected = (1..60).joinToString("\n") { "expected line $it" }
      val actual = (1..60).joinToString("\n") { if (it == 30) "MUTATED line 30" else "expected line $it" }

      val result = StringEq.equals(actual, expected, EqContext()) as EqResult.Failure
      result.error().message shouldContain "[Change at line"
   }
})
