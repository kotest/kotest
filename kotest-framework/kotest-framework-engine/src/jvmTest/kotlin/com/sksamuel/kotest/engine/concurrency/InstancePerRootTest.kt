package com.sksamuel.kotest.engine.concurrency

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.concurrent.getOrSet

private val externalMultipleThreadCounter = PersistentThreadLocal<Int>()

class InstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot

   val multipleThreadCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      assertSoftly {
         multipleThreadCounter.map shouldHaveSize 1
         multipleThreadCounter.map.values.sum() shouldBe 1
      }
   }

   afterProject {
      assertSoftly {
         externalMultipleThreadCounter.map shouldHaveSize 3
         externalMultipleThreadCounter.map.values.sum() shouldBe 3
      }
   }

   test("test 1 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 2 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 3 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

})
