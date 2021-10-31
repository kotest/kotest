package com.sksamuel.kotest.engine.test.names

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

// some characters are being filtered out of ids and breaking nested tests
// https://github.com/kotest/kotest/issues/1828
class TestNameUnicodeTest : DescribeSpec({

   var count = 0

   afterSpec {
      count shouldBe 2
   }

   describe("test ids should work for non ascii characters") {
      it("☆") {
         count++
      }
      describe("♬") {
         it("♪") {
            count++
         }
      }
   }
})
