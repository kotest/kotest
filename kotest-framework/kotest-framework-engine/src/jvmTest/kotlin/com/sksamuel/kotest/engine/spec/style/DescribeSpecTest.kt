package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.spec.style.DescribeSpec
import kotlin.time.Duration.Companion.milliseconds

class DescribeSpecTest : DescribeSpec() {

   init {
      context("a context") {
         describe("a describe") {
         }
         xdescribe("an ignored describe") {
            error("boom")
         }
         xdescribe("an ignored describe with config").config(enabled = false) {
            error("boom")
         }
         it("an inner test") {
         }
         it("an inner test with config").config(timeout = 12343.milliseconds) {
         }
         xit("an ignored test") {
            error("boom")
         }
         xit("an ignored test with config").config(timeout = 12343.milliseconds) {
         }
      }
      describe("a describe") {
      }
      xdescribe("an ignored describe") {
         error("boom")
      }
      xdescribe("an ignored describe with config").config(enabled = false) {
         error("boom")
      }
      it("an inner test") {
      }
      it("an inner test with config").config(timeout = 12343.milliseconds) {
      }
      xit("an ignored test") {
         error("boom")
      }
      xit("an ignored test with config").config(timeout = 12343.milliseconds) {
      }
   }
}
