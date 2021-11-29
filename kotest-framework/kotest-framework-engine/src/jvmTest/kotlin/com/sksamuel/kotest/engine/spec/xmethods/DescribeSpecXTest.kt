package com.sksamuel.kotest.engine.spec.xmethods

import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecXTest : DescribeSpec() {

   init {

      xdescribe("xdescribe should be skipped") {
         error("")
      }

      describe("in describe") {
         xit("xit should be skipped") {
            error("")
         }
      }

      describe("a describe") {
         describe("in context") {
            xit("xit should be skipped") {
               error("")
            }
         }
      }
   }
}
