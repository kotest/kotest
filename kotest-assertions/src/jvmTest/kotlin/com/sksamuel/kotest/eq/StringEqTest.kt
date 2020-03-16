package com.sksamuel.kotest.eq

import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.eq.StringEq
import io.kotest.matchers.shouldBe

class StringEqTest : FunSpec({
   test("string eq should highlight line break diffs") {
      StringEq.equals("foo\nbar\r", "\r\nfoo\nbar\r\n")?.message shouldBe """expected: \r\nfoo\nbar\r\n but was: foo\nbar\r
(contents match, but line-breaks differ; output has been escaped to show line-breaks)"""
   }
})
