package com.sksamuel.kotest.engine.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.locks.ReentrantLock

@EnabledIf(LinuxOnlyGithubCondition::class)
class WithLocksNestedSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot

   val lock = ReentrantLock()

   context("First single thread context") {

      test("test should lock object") {
         lock.lock()
         Thread.sleep(1000)
         lock.unlock()
      }

      test("lock should be unlocked") {
         Thread.sleep(300)
         lock.isLocked shouldBe false
      }

      test("lock should be unlocked too") {
         Thread.sleep(300)
         shouldThrow<AssertionError> {
            lock.isLocked shouldBe true
         }
      }

      context("First inner single thread context") {

         test("lock should be unlocked") {
            lock.isLocked shouldBe false
         }
      }
   }

})
