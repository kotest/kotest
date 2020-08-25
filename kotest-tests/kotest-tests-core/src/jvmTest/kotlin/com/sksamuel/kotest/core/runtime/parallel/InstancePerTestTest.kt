@file:Suppress("unused", "NAME_SHADOWING")

package com.sksamuel.kotest.core.runtime.parallel

import com.sksamuel.kotest.core.runtime.PersistentThreadLocal
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.getOrSet

private val externalMultipleThreadCounter =
   PersistentThreadLocal<Int>()


class SpecThreadInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val multipleThreadCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      assertSoftly {
         multipleThreadCounter.map shouldHaveSize 1
         multipleThreadCounter.map.values.sum() shouldBe 1
      }
   }

   afterProject {
      assertSoftly {
         externalMultipleThreadCounter.map shouldHaveSize 3
         externalMultipleThreadCounter.map.values.sum() shouldBe 3
      }
   }

   test("test 1 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 2 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

   test("test 3 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      val externalCounter = externalMultipleThreadCounter.getOrSet { 0 }
      externalMultipleThreadCounter.set(externalCounter + 1)
   }

})

private val objects = ConcurrentHashMap.newKeySet<ReentrantLock>()

class SpecThreadInstancePerTestWithLockTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
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

private val externalThreadAccum =
   PersistentThreadLocal<String>()

class SpecThreadWithNestedTestInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val innerThreadAccum =
      PersistentThreadLocal<String>()

   afterSpec {
      assertSoftly {
         innerThreadAccum.map shouldHaveSize 1
         innerThreadAccum.map.values.shouldContainAnyOf("a", "aa", "b", "bb", "c", "cc", "cd", "cdd")
      }
   }

   afterProject {
      assertSoftly {
         externalThreadAccum.map.shouldHaveSize(3)
         externalThreadAccum.map.values.shouldContainExactlyInAnyOrder("aaaaa", "bbbbb", "ccccccdcddcdd")
      }
   }

   context("First single thread context") {
      val externalAccum = externalThreadAccum.getOrSet { "" }
      externalThreadAccum.set(externalAccum + "a")

      val accum = innerThreadAccum.getOrSet { "" }
      innerThreadAccum.set(accum + "a")

      test("test 1 should create own key in map with value a") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "a")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "a")
      }

      test("test 2 should create own key in map with value 1") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "a")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "a")
      }
   }

   context("Second single thread context") {

      val externalAccum = externalThreadAccum.getOrSet { "" }
      externalThreadAccum.set(externalAccum + "b")

      val accum = innerThreadAccum.getOrSet { "" }
      innerThreadAccum.set(accum + "b")

      test("test 1 should create key in map or add value b") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "b")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "b")
      }

      test("test 2 should create key in map or add value b") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "b")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "b")
      }
   }

   context("Third single thread context") {

      val externalAccum = externalThreadAccum.getOrSet { "" }
      externalThreadAccum.set(externalAccum + "c")

      val accum = innerThreadAccum.getOrSet { "" }
      innerThreadAccum.set(accum + "c")

      test("test 1 should create new key in map for context or add value c") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "c")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "c")
      }

      test("test 2 should create new key in map for context or add value c") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "c")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "c")
      }

      context("First inner single thread context") {
         val externalAccum = externalThreadAccum.getOrSet { "" }
         externalThreadAccum.set(externalAccum + "d")

         val accum = innerThreadAccum.getOrSet { "" }
         innerThreadAccum.set(accum + "d")

         test("test 1 should create new key in map for context or add value d") {
            val externalAccum = externalThreadAccum.getOrSet { "" }
            externalThreadAccum.set(externalAccum + "d")

            val accum = innerThreadAccum.getOrSet { "" }
            innerThreadAccum.set(accum + "d")
         }

         test("test 2 should create new key in map for context or add value d") {
            val externalAccum = externalThreadAccum.getOrSet { "" }
            externalThreadAccum.set(externalAccum + "d")

            val accum = innerThreadAccum.getOrSet { "" }
            innerThreadAccum.set(accum + "d")
         }
      }
   }

})

private val locks = ConcurrentHashMap.newKeySet<ReentrantLock>()

class SpecThreadWithNestedTestWithLockInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val outerContextLock = ReentrantLock()

   afterProject {
      locks shouldHaveSize 12
   }

   context("First thread context") {
      //This code a test by itself that's why objects will be added one more time if compare with InstancePerLeaf
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
            delay(1000)
         } finally {
            outerContextLock.unlock()
            innerLock.unlock()
         }
      }

      test("lock should be unlocked") {
         println(Thread.currentThread().name)

         locks.add(innerLock)
         locks.add(outerContextLock)
         delay(300)
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
            delay(1000)
         } finally {
            outerContextLock.unlock()
            innerLock.unlock()
         }
      }

      test("lock should be unlocked") {
         println(Thread.currentThread().name)

         locks.add(innerLock)
         locks.add(outerContextLock)
         delay(300)
         outerContextLock.isLocked shouldBe false
         innerLock.isLocked shouldBe false
      }

   }
})


private val beforeTestCounter = AtomicInteger(0)
private val afterTestCounter = AtomicInteger(0)
private val beforeSpecCounter = AtomicInteger(0)
private val afterSpecCounter = AtomicInteger(0)

class SpecThreadBeforeAfterInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

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

   test("test 1 should run before/after test one more time") {
      "void"
   }

   test("test 2 should run before/after test one more time") {
      "void"
   }

   test("test 3 should run before/after test one more time") {
      "void"
   }
})

private val beforeTestNestedCounter = AtomicInteger(0)
private val afterTestNestedCounter = AtomicInteger(0)
private val beforeSpecNestedCounter = AtomicInteger(0)
private val afterSpecNestedCounter = AtomicInteger(0)

class SpecThreadWithNestedBeforeAfterInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   beforeSpec {
      beforeSpecNestedCounter.getAndIncrement()
   }

   beforeTest {
      beforeTestNestedCounter.getAndIncrement()
   }

   afterTest {
      afterTestNestedCounter.getAndIncrement()
   }

   afterSpec {
      afterSpecNestedCounter.getAndIncrement()
   }

   afterProject {
      assertSoftly {
         beforeSpecNestedCounter.get() shouldBe 9
         afterSpecNestedCounter.get() shouldBe 9
         beforeTestNestedCounter.get() shouldBe 18
         afterTestNestedCounter.get() shouldBe 18
      }
   }

   context("First single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 2 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      context("Inner First context") {
         "context scope is a test and run before/after test 2 time"
         "one time for outer context scope and 1 time for the Inner context scope (itself0"

         test("test 3 from Inner First context should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
         test("test 4 from Inner First context should run before/after test 3 times") {
            "one time for outer context scope, 1 time for Inner context scope and 1 for the test itself"
         }
      }
   }

   context("Second single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }

      test("test 2 should run before/after test 2 times") {
         "one time for context scope and 1 time for the test itself"
      }
   }
})

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
      "void"
   }

   test("test 2 should run before/after test concurrently and independent") {
      "void"
   }

   test("test 3 should run before/after test concurrently and independent") {
      "void"
   }
})


private val lockedCounterTestExtensionConcurrent = AtomicInteger(0)
private val counterTestExtensionConcurrent = AtomicInteger(0)

class SpecThreadTestCaseExtensionConcurrentInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val lock = ReentrantLock()

   extension { (testCase, execute) ->
      val isLockAcquired = lock.tryLock()
      if (isLockAcquired) {
         lock.lock()
         try {
            delay(300)
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
      "void"
   }

   test("test 2 should run TestCaseExtension concurrently and independent") {
      "void"
   }

   test("test 3 should run TestCaseExtension concurrently and independent") {
      "void"
   }
})

private val aroundSpecCounter = AtomicInteger(0)
private val lockedCounterSpecExtensionConcurrent = AtomicInteger(0)

class SpecThreadSpecExtensionConcurrentInstancePerTestTest : FunSpec({

   isolationMode = IsolationMode.InstancePerTest
   threads = 3

   val lock = ReentrantLock()

   aroundSpec { (_, process) ->
      val isLockAcquired = lock.tryLock()
      if (isLockAcquired) {
         lock.lock()
         try {
            delay(300)
         } finally {
            lock.unlock()
         }
      } else {
         lockedCounterSpecExtensionConcurrent.getAndIncrement()
      }

      aroundSpecCounter.getAndIncrement()
      process()
   }

   afterProject {
      lockedCounterSpecExtensionConcurrent.get() shouldBeExactly 0
      aroundSpecCounter.get() shouldBeExactly 1
   }

   test("test 1 aroundSpecExtension should be called only once for entire Spec without concurrency") {
      "void"
   }

   test("test 2 aroundSpecExtension should be called only once for entire Spec without concurrency") {
      "void"
   }

   test("test 3 aroundSpecExtension should be called only once for entire Spec without concurrency") {
      "void"
   }
})

