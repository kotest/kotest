package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private val lockedCounterTestExtensionConcurrent = AtomicInteger(0)
private val counterTestExtensionConcurrent = AtomicInteger(0)

class TestCaseExtensionConcurrentInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot
//   threads = 3

   val lock = ReentrantLock()

   extension { (testCase, execute) ->
      val isLockAcquired = lock.tryLock()
      if (isLockAcquired) {
         lock.lock()
         try {
            Thread.sleep(300)
         } finally {
            lock.unlock()
         }
      } else {
         lockedCounterTestExtensionConcurrent.getAndIncrement()
      }
      counterTestExtensionConcurrent.getAndIncrement()
      execute(testCase)
   }

   afterProject {
      lockedCounterTestExtensionConcurrent.get() shouldBe 0
      counterTestExtensionConcurrent.get() shouldBe 3
   }

   test("test 1 should run TestCaseExtension concurrently and independent") {
   }

   test("test 2 should run TestCaseExtension concurrently and independent") {
   }

   test("test 3 should run TestCaseExtension concurrently and independent") {
   }
})
