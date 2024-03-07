package com.sksamuel.kotest.engine.threads

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private val lockedCounter = AtomicInteger(0)
private val counterBeforeTestConcurrent = AtomicInteger(0)

class SpecThreadBeforeTestConcurrentInstancePerLeafTest : FunSpec({

   isolationMode = IsolationMode.InstancePerLeaf
   threads = 3

   val lock = ReentrantLock()

   beforeTest {
      val isLockAcquired = lock.tryLock()
      if (isLockAcquired) {
         lock.lock()
         try {
            Thread.sleep(300)
         } finally {
            lock.unlock()
         }
      } else {
         lockedCounter.getAndIncrement()
      }
      counterBeforeTestConcurrent.getAndIncrement()
   }

   afterProject {
      lockedCounter.get() shouldBe 0
      counterBeforeTestConcurrent.get() shouldBe 3
   }

   test("test 1 should run before/after test concurrently and independent") {
   }

   test("test 2 should run before/after test concurrently and independent") {
   }

   test("test 3 should run before/after test concurrently and independent") {
   }
})
