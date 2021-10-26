package com.sksamuel.kotest.engine.threads

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val beforeSpecNestedCounter = AtomicInteger(0)
private val beforeTestNestedCounter = AtomicInteger(0)
private val afterTestNestedCounter = AtomicInteger(0)
private val afterSpecNestedCounter = AtomicInteger(0)

class SpecThreadWithNestedBeforeAfterInstancePerLeafTest : FunSpec({

   isolationMode = IsolationMode.InstancePerLeaf
   threads = 3

   beforeSpec {
      beforeSpecNestedCounter.getAndIncrement()
   }

   beforeTest {
      beforeTestNestedCounter.getAndIncrement()
   }

   afterTest {
      afterTestNestedCounter.getAndIncrement()
   }

   afterSpec {
      afterSpecNestedCounter.getAndIncrement()
   }

   afterProject {
      assertSoftly {
         beforeSpecNestedCounter.get() shouldBe 6
         afterSpecNestedCounter.get() shouldBe 6
         beforeTestNestedCounter.get() shouldBe 14
         afterTestNestedCounter.get() shouldBe 14
      }
   }

   context("First single thread context") {
      "context scope is NOT a test if compare with InstancePerTest and don't run before/after test"

      test("test 1 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 2 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      context("Inner First context") {
         "context scope is NOT a test if compare with InstancePerTest"
         test("test 3 should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
         test("test 4 should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
      }
   }

   context("Second single thread context") {
      "context scope is NOT a test if compare with InstancePerTest"

      test("test 5 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 6 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }
   }
})
