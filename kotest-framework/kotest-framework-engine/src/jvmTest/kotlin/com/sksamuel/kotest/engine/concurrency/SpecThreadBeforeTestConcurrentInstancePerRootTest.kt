package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

private val lockedCounter = AtomicInteger(0)

@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecThreadBeforeTestConcurrentInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot
//   threads = 3

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
