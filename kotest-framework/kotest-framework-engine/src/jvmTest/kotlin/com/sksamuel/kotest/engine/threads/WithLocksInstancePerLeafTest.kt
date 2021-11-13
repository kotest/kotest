package com.sksamuel.kotest.engine.threads

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

private val objects = ConcurrentHashMap.newKeySet<ReentrantLock>()

class WithLocksInstancePerLeafTest : FunSpec({

   isolationMode = IsolationMode.InstancePerLeaf
   threads = 3

   val lock = ReentrantLock()

   afterProject {
      //Different objects - for each thread each own lock
      objects shouldHaveSize 3
   }

   test("test should lock object") {
      objects.add(lock)
      lock.lock()
      try {
         delay(1000)
      } finally {
         lock.unlock()
      }

   }

   test("lock should be unlocked because lock object is different") {
      objects.add(lock)
      delay(300)
      lock.isLocked shouldBe false
   }

   test("lock should be unlocked too") {
      objects.add(lock)
      delay(300)
      shouldThrow<AssertionError> {
         lock.isLocked shouldBe true
      }
   }

})
