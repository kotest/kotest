package com.sksamuel.kotest.engine

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

// some characters are being filtered out of ids and breaking nested tests
// https://github.com/kotest/kotest/issues/1828
class TestIdTest : DescribeSpec({
   var count = 0

   afterSpec {
      count shouldBe 2
   }

   context("one_two") {
      beforeEach { // should not fire as there are no nested contexts here
         count++
      }
   }

   context("one two") {
      it("count is 1") { // the other before each should not fire
         count++
      }
   }

   context("one two!") {
      it("count is 2") { // the other before each should not fire
         count++
      }
   }
})
