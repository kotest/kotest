package com.sksamuel.kotest.engine.threads

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private val lockedCounter = AtomicInteger(0)

class SpecThreadBeforeTestConcurrentInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val lock = ReentrantLock()

   beforeTest {
      val isLockAcquired = lock.tryLock()
      if (isLockAcquired) {
         lock.lock()
         try {
            delay(300)
         } finally {
            lock.unlock()
         }
      } else {
         lockedCounter.getAndIncrement()
      }
   }

   afterProject {
      lockedCounter.get() shouldBe 0
   }

   test("test 1 should run before/after test concurrently and independent") {
   }

   test("test 2 should run before/after test concurrently and independent") {
   }

   test("test 3 should run before/after test concurrently and independent") {
   }
})
