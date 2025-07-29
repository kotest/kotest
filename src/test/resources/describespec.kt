package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecExample : DescribeSpec() {
   init {
      describe("describe block") {
         it("it block") {
            // test here
         }
         xit("xit block") {
            // test here
         }
         it("it with config").config(invocations = 2) {
            // test here
         }
         xit("xit block with config").config(invocations = 2) {
            // test here
         }
         describe("nested describe block") {
            it("it block") {
               // test here
            }
         }
         xdescribe("nested xdescribe block") {
            it("it block") {
               // test here
            }
         }
      }
      xdescribe("xdescribe block") {
         it("it block") {
            // test here
         }
         xit("xit block") {
            // test here
         }
         it("it with config").config(invocations = 2) {
            // test here
         }
         xit("xit block with config").config(invocations = 2) {
            // test here
         }
         describe("nested describe block") {
            it("it block") {
               // test here
            }
         }
         xdescribe("nested xdescribe block") {
            it("it block") {
               // test here
            }
         }
      }
      context("context block") {
         context("nested context block") {
            describe("nested describe block") {
               it("it block") {
                  // test here
               }
            }
            xdescribe("nested xdescribe block") {
               it("it block") {
                  // test here
               }
            }
         }
         xcontext("nested xcontext block") {
            describe("nested describe block") {
               it("it block") {
                  // test here
               }
            }
            xdescribe("nested xdescribe block") {
               it("it block") {
                  // test here
               }
            }
         }
         describe("nested describe block") {
            it("it block") {
               // test here
            }
         }
      }
      describe("describe with config").config(enabled = true) {
         it("it block") {
            // test here
         }
      }
      xdescribe("xdescribe with config").config(enabled = true) {
         it("it block") {
            // test here
         }
      }
      context("context with config").config(enabled = true) {
         describe("nested describe with config").config(enabled = true) {
            it("it block") {
               // test here
            }
         }
      }
      xcontext("xcontext with config").config(enabled = true) {
      }
   }
}

