package com.sksamuel.kotest.eq

import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.eq.StringEq
import io.kotest.assertions.shouldFailWithMessage
import io.kotest.matchers.shouldBe

class StringEqTest : FunSpec({
   test("string eq should highlight line break diffs") {
      StringEq.equals("foo\nbar\r", "\r\nfoo\nbar\r\n")?.message shouldBe """
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
})
