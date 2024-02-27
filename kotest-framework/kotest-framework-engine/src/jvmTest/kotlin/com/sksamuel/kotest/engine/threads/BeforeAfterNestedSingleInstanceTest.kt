package com.sksamuel.kotest.engine.threads

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val beforeTestNestedCounter = AtomicInteger(0)
private val afterTestNestedCounter = AtomicInteger(0)
private val beforeSpecNestedCounter = AtomicInteger(0)
private val afterSpecNestedCounter = AtomicInteger(0)

class SpecThreadWithNestedBeforeAfterSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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
         beforeSpecNestedCounter.get() shouldBe 1
         afterSpecNestedCounter.get() shouldBe 1
         beforeTestNestedCounter.get() shouldBe 9
         afterTestNestedCounter.get() shouldBe 9
      }
   }

   context("First single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 1 time") {
         "only for the test itself"
      }

      test("test 2 should run before/after test 1 time") {
         "only for the test itself"
      }

      context("Inner First context") {
         "context scope is a test and run before/after test 1 time"

         test("test 3 from Inner First context should run before/after test 1 time") {
            "only for the test itself"
         }
         test("test 4 from Inner First context should run before/after test 1 time") {
            "only for the test itself"
         }
      }
   }

   context("Second single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 1 time") {
         "only for the test itself"
      }

      test("test 2 should run before/after test 1 time") {
         "only for the test itself"
      }
   }
})
