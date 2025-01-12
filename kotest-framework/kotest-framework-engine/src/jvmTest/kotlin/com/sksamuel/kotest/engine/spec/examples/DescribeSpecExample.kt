package com.sksamuel.kotest.engine.spec.examples

import io.kotest.assertions.fail
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class DescribeSpecExampleTest : FunSpec({
   coroutineTestScope = true

   test("expect tests run") {
      val collector = CollectingTestEngineListener()

      val duration = testTimeSource().measureTime {
         TestEngineLauncher(collector)
            .withClasses(DescribeSpecExample::class)
            .async()
      }

      duration shouldBe 0.seconds

      collector.tests.keys.map { it.descriptor.id.value }.shouldContainExactlyInAnyOrder(
         "a context is like a describe",
         "a describe with config",
         "a disabled context",
         "a nested describe!",
         "disabled describe",
         "disabled test with config",
         "disabled test without describe",
         "disabled test",
         "disabled test",
         "disabled top level context",
         "disabled top level describe with config",
         "disabled top level describe",
         "some thing",
         "test name 2",
         "test name 2",
         "test name 2",
         "test name 2",
         "test name",
         "test name",
         "test name",
         "test name",
         "test name",
         "test name",
         "test name",
         "test name",
         "test without describe",
         "top level context",
         "with some context",
         "with some context",
         "with some describe",
         "with some describe",
      )
   }
})

private class DescribeSpecExample : DescribeSpec() {
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
         describe("with some describe") {
            it("test name") {
               // test here
            }
            it("test name 2").config(enabled = false) {
               // test here
            }
            describe("with some context") {
               it("test name") {
                  // test here
               }
               it("test name 2").config(timeout = 1512.milliseconds) {
                  // test here
               }
            }
         }
         context("a context is like a describe") {
            it("test name") {
               // test here
            }
         }
         xcontext("a disabled context") {
            it("test name") {
               // test here
            }
         }
         xdescribe("disabled describe") {
            fail("should not be invoked")
         }
      }

      describe("a describe with config").config(timeout = 234.milliseconds) {
         describe("with some describe") {
            it("test name") {
               // test here
            }
            it("test name 2").config(enabled = true) {
               // test here
            }
            describe("with some context") {
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

      xdescribe("disabled top level describe with config").config(timeout = 123.milliseconds) {
         fail("should not be invoked")
      }

      context("top level context") {
         it("test name") {
            // test here
         }
         xit("disabled test with config").config(invocations = 3) {
            fail("should not be invoked")
         }
      }

      xcontext("disabled top level context") {
         fail("should not be invoked")
      }

      it("test without describe") {
         // test here
      }

      xit("disabled test without describe") {
         fail("should not be invoked")
      }
   }
}

