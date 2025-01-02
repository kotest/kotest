package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

private val locks = ConcurrentHashMap.newKeySet<ReentrantLock>()

class SpecThreadWithNestedTestWithLockInstancePerRootTest : FunSpec({

   isolationMode = IsolationMode.InstancePerRoot

   val outerContextLock = ReentrantLock()

   afterProject {
      locks shouldHaveSize 4
   }

   context("First thread context") {
      //This code a test by itself that's why objects will be added one more time if compare with InstancePerRoot
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

