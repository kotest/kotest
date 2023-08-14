package com.sksamuel.kotest.engine.threads

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

private val locks = ConcurrentHashMap.newKeySet<ReentrantLock>()

class NestedTestsWithLockInstancePerLeafTest : FunSpec({

   isolationMode = IsolationMode.InstancePerLeaf
   threads = 2

   val outerContextLock = ReentrantLock()

   afterProject {
      locks shouldHaveSize 8
   }

   context("First thread context") {
      val innerLock = ReentrantLock()
      locks.add(innerLock)
      locks.add(outerContextLock)

      test("test should lock object") {
         println(Thread.currentThread().name)

         //The same objects from context scope
         locks.add(innerLock)
         locks.add(outerContextLock)

         innerLock.lock()
         outerContextLock.lock()
         try {
            Thread.sleep(1000)
         } finally {
            outerContextLock.unlock()
            innerLock.unlock()
         }

      }

      test("lock should be unlocked because lock object is different") {
         println(Thread.currentThread().name)

         locks.add(innerLock)
         locks.add(outerContextLock)
         Thread.sleep(300)
         outerContextLock.isLocked shouldBe false
         innerLock.isLocked shouldBe false
      }
   }

   context("Second single thread context") {
      val innerLock = ReentrantLock()
      locks.add(innerLock)
      locks.add(outerContextLock)
      test("test should lock object") {
         println(Thread.currentThread().name)

         locks.add(innerLock)
         locks.add(outerContextLock)

         innerLock.lock()
         outerContextLock.lock()
         try {
            Thread.sleep(1000)
         } finally {
            outerContextLock.unlock()
            innerLock.unlock()
         }
      }

      test("lock should be unlocked") {
         println(Thread.currentThread().name)

         locks.add(innerLock)
         locks.add(outerContextLock)
         Thread.sleep(300)
         outerContextLock.isLocked shouldBe false
         innerLock.isLocked shouldBe false
      }

   }
})
