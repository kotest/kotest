package com.sksamuel.kotest.engine.threads

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val beforeTestCounter = AtomicInteger(0)
private val afterTestCounter = AtomicInteger(0)
private val beforeSpecCounter = AtomicInteger(0)
private val afterSpecCounter = AtomicInteger(0)

class SpecThreadBeforeAfterSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
//   threads = 3

   beforeSpec {
      beforeSpecCounter.getAndIncrement()
   }

   beforeTest {
      beforeTestCounter.getAndIncrement()
   }

   afterTest {
      afterTestCounter.getAndIncrement()
   }

   afterSpec {
      afterSpecCounter.getAndIncrement()
   }

   afterProject {
      beforeSpecCounter.get() shouldBe 1
      afterSpecCounter.get() shouldBe 1
      beforeTestCounter.get() shouldBe 3
      afterTestCounter.get() shouldBe 3
   }

   test("test 1 should run before/after test one more time") {
   }

   test("test 2 should run before/after test one more time") {
   }

   test("test 3 should run before/after test one more time") {
   }
})
