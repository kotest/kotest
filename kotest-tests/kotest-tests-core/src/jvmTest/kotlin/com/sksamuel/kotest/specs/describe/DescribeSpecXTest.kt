package com.sksamuel.kotest.specs.describe

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecXTest : DescribeSpec() {

   init {

      xdescribe("xdescribe should be skipped") {
         attemptToFail()
      }

      describe("in describe") {
         xit("xit should be skipped") {
            attemptToFail()
         }
      }

      describe("a describe") {
         describe("in context") {
            xit("xit should be skipped") {
               attemptToFail()
            }
         }
      }
   }
}
