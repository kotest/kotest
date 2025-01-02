package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val beforeTestCounter = AtomicInteger(0)
private val afterTestCounter = AtomicInteger(0)
private val beforeSpecCounter = AtomicInteger(0)
private val afterSpecCounter = AtomicInteger(0)

class BeforeAfterInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot
   testExecutionMode = TestExecutionMode.Concurrent

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
      beforeSpecCounter.get() shouldBe 3
      afterSpecCounter.get() shouldBe 3
      beforeTestCounter.get() shouldBe 3
      afterTestCounter.get() shouldBe 3
   }

   test("test 1 should run before/after test") {
   }

   test("test 2 should run before/after test") {
   }

   test("test 3 should run before/after test") {
   }
})
