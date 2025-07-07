package com.sksamuel.kotest.eq

import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.eq.StringEq
import io.kotest.assertions.shouldFailWithMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder

class StringEqTest : FunSpec({
   test("string eq should highlight line break diffs") {
      StringEq.equals("foo\nbar\r", "\r\nfoo\nbar\r\n", false)?.message shouldBe """
         |(contents match, but line-breaks differ; output has been escaped to show line-breaks)
         |expected:<\r\nfoo\nbar\r\n> but was:<foo\nbar\r>
         """.trimMargin()
   }

   test("StringEq shows type information when relevant") {
      shouldFailWithMessage("expected:java.lang.StringBuilder<foo> but was:kotlin.String<\"foo\">") {
         "foo" shouldBe StringBuilder("foo")
      }
   }

   test("String comparison with same types dont include type information") {
      shouldFailWithMessage("expected:<\"bar\"> but was:<\"foo\">") {
         "foo" shouldBe "bar"
      }
   }

   test("Find partial match for string") {
      val expected = "One quick brown fox jumps over the lazy cat"
      val value ="The quick brown fox jumps over the lazy dog"
      val actual = shouldThrow<AssertionError> { value shouldBe expected }.message
      actual.shouldContainInOrder(
         "Match[0]: part of slice with indexes [2..39] matched actual[2..39]",
         """Line[0] ="The quick brown fox jumps over the lazy dog"""",
         """Match[0]= --++++++++++++++++++++++++++++++++++++++---""",
         """expected:<"One quick brown fox jumps over the lazy cat"> but was:<"The quick brown fox jumps over the lazy dog">"""
      )
   }
})
