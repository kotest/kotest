package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val beforeTestNestedCounter = AtomicInteger(0)
private val afterTestNestedCounter = AtomicInteger(0)
private val beforeSpecNestedCounter = AtomicInteger(0)
private val afterSpecNestedCounter = AtomicInteger(0)

class BeforeAfterNestedInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot

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
      beforeSpecNestedCounter.get() shouldBe 2
      afterSpecNestedCounter.get() shouldBe 2
      beforeTestNestedCounter.get() shouldBe 9
      afterTestNestedCounter.get() shouldBe 9
   }

   context("First single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 2 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      context("Inner First context") {
         "context scope is a test and run before/after test 2 time"
         "one time for outer context scope and 1 time for the Inner context scope (itself0"

         test("test 3 from Inner First context should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
         test("test 4 from Inner First context should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
      }
   }

   context("Second single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 2 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }
   }
})
