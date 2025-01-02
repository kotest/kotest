package com.sksamuel.kotest.engine.concurrency

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.concurrent.getOrSet

private val externalMultipleThreadCounter = PersistentThreadLocal<Int>()

class SpecInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot
   testExecutionMode = TestExecutionMode.Concurrent

   val internalThreadCounter = PersistentThreadLocal<Int>()

   afterSpec {
      assertSoftly {
         internalThreadCounter.map shouldHaveSize 1
         internalThreadCounter.map.values.sum() shouldBe 1
      }
   }

   afterProject {
      assertSoftly {
         externalMultipleThreadCounter.map shouldHaveSize 1
         externalMultipleThreadCounter.map.values.sum() shouldBe 1
      }
   }

   test("test 1 should create own key in external map with value 1") {
      val counter = internalThreadCounter.getOrSet { 0 }
      internalThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 2 should create own key in external map with value 1") {
      val counter = internalThreadCounter.getOrSet { 0 }
      internalThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 3 should create own key in external map with value 1") {
      val counter = internalThreadCounter.getOrSet { 0 }
      internalThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

})
