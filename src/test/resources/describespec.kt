package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecExample : DescribeSpec() {
   init {
      describe("some thing") {
         it("test name") {
            // test here
         }
         xdescribe("xignored describe") {

         }
         describe("with some context") {
            it("test name") {
               // test here
            }
            it("test name 2").config(invocations = 2) {
               // test here
            }
            describe("yet another context") {
               it("test name") {
                  // test here
               }
               xit("xignored test") {
                  // test here
               }
               it("test name 2").config(invocations = 2) {
                  // test here
               }
               xit("xignored test with config").config(invocations = 2) {
                  // test here
               }
            }
         }
      }
   }
}

