package com.sksamuel.kotest.specs.describe

import io.kotest.assertions.fail
import io.kotest.core.spec.style.DescribeSpec
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class DescribeSpecExample : DescribeSpec() {
   init {
      describe("some thing") {
         it("test name") {
            // test here
         }
         xit("disabled test") {
            fail("should not be invoked")
         }
         describe("a nested describe!") {
            it("test name") {
               // test here
            }
            xit("disabled test") {
               fail("should not be invoked")
            }
         }
         context("with some context") {
            it("test name") {
               // test here
            }
            it("test name 2").config(enabled = false) {
               // test here
            }
            context("with some context") {
               it("test name") {
                  // test here
               }
               it("test name 2").config(timeout = 1512.milliseconds) {
                  // test here
               }
            }
         }
         xdescribe("disabled describe") {
            fail("should not be invoked")
         }
      }

      describe("some other thing") {
         context("with some context") {
            it("test name") {
               // test here
            }
            it("test name 2").config(enabled = true) {
               // test here
            }
            context("with some context") {
               it("test name") {
                  // test here
               }
               it("test name 2").config(timeout = 1512.milliseconds) {
                  // test here
               }
            }
         }
      }

      xdescribe("disabled top level describe") {
         fail("should not be invoked")
      }
   }
}

