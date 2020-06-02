@file:Suppress("unused", "NAME_SHADOWING")

package com.sksamuel.kotest.core.runtime.parallel

import com.sksamuel.kotest.core.runtime.PersistentThreadLocal
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
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

class SpecThreadSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
   threads = 3

   val multipleThreadCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      multipleThreadCounter.map.shouldHaveSize(3)
      multipleThreadCounter.map.values.sum() shouldBe 6
   }

   test("test 1 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

   test("test 2 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

   test("test 3 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

   test("test 4 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

   test("test 5 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

   test("test 6 should create own key in map with value 1") {
      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)
   }

})

private val objects = ConcurrentHashMap.newKeySet<ReentrantLock>()

class SpecThreadSingleInstanceWithLockTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
   threads = 3

   val lock = ReentrantLock()

   afterProject {
      //The same object
      objects shouldHaveSize 1
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

   test("lock should be locked") {
      objects.add(lock)
      delay(300)
      lock.isLocked shouldBe true
   }

   test("lock should be locked too") {
      objects.add(lock)
      delay(300)
      shouldThrow<AssertionError> {
         lock.isLocked shouldBe false
      }
   }

})

class SpecThreadWithNestedTestSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
   threads = 3

   val multipleThreadAccum =
      PersistentThreadLocal<String>()

   afterSpec {
      assertSoftly {
         multipleThreadAccum.map.shouldHaveSize(3)
         multipleThreadAccum.map.values.shouldContainExactlyInAnyOrder("aa", "bb", "ccdd")
      }

   }

   context("First single thread context") {
      test("test 1 should create new key in map for context or add value a") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "a")
      }

      test("test 2 should create new key in map for context or add value a") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "a")
      }
   }

   context("Second single thread context") {
      test("test 1 should create new key in map for context or add value b") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "b")
      }

      test("test 2 should create new key in map for context or add value b") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "b")
      }
   }

   context("Third single thread context") {
      test("test 1 should create new key in map for context or add value c") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "c")
      }

      test("test 2 should create new key in map for context or add value c") {
         val accum = multipleThreadAccum.getOrSet { "" }
         multipleThreadAccum.set(accum + "c")
      }

      context("First inner single thread context") {
         test("test 1 should create new key in map for context or add value d") {
            val accum = multipleThreadAccum.getOrSet { "" }
            multipleThreadAccum.set(accum + "d")
         }

         test("test 2 should create new key in map for context or add value d") {
            val accum = multipleThreadAccum.getOrSet { "" }
            multipleThreadAccum.set(accum + "d")
         }
      }
   }

})

class SpecThreadWithNestedTestWithLockSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
   threads = 3

   val lock = ReentrantLock()

   context("First single thread context") {

      test("test should lock object") {
         lock.lock()
         delay(1000)
         lock.unlock()
      }

      test("lock should be unlocked") {
         delay(300)
         lock.isLocked shouldBe false
      }

      test("lock should be unlocked too") {
         delay(300)
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

private val beforeTestCounter = AtomicInteger(0)
private val afterTestCounter = AtomicInteger(0)
private val beforeSpecCounter = AtomicInteger(0)
private val afterSpecCounter = AtomicInteger(0)

class SpecThreadBeforeAfterSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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
      beforeSpecCounter.get() shouldBe 1
      afterSpecCounter.get() shouldBe 1
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

class SpecThreadWithNestedBeforeAfterSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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
         beforeSpecNestedCounter.get() shouldBe 1
         afterSpecNestedCounter.get() shouldBe 1
         beforeTestNestedCounter.get() shouldBe 9
         afterTestNestedCounter.get() shouldBe 9
      }
   }

   context("First single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 1 time") {
         "only for the test itself"
      }

      test("test 2 should run before/after test 1 time") {
         "only for the test itself"
      }

      context("Inner First context") {
         "context scope is a test and run before/after test 1 time"

         test("test 3 from Inner First context should run before/after test 1 time") {
            "only for the test itself"
         }
         test("test 4 from Inner First context should run before/after test 1 time") {
            "only for the test itself"
         }
      }
   }

   context("Second single thread context") {
      "context scope is a test and run before/after test 1 time"

      test("test 1 should run before/after test 1 time") {
         "only for the test itself"
      }

      test("test 2 should run before/after test 1 time") {
         "only for the test itself"
      }
   }
})

private val lockedCounter = AtomicInteger(0)
private val counterBeforeTestConcurrent = AtomicInteger(0)

class SpecThreadBeforeTestConcurrentSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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

      counterBeforeTestConcurrent.getAndIncrement()
   }

   afterProject {
      lockedCounter.get() shouldBe 2
      counterBeforeTestConcurrent.get() shouldBe 3
   }

   test("test 1 should run before/after test concurrently") {
      println(Thread.currentThread().name)
   }

   test("test 2 should run before/after test concurrently") {
      println(Thread.currentThread().name)
   }

   test("test 3 should run before/after test concurrently") {
      println(Thread.currentThread().name)
   }
})

private val lockedCounterTestExtensionConcurrent = AtomicInteger(0)
private val counterTestExtensionConcurrent = AtomicInteger(0)

class SpecThreadTestCaseExtensionConcurrentSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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
      lockedCounterTestExtensionConcurrent.get() shouldBe 2
      counterTestExtensionConcurrent.get() shouldBe 3
   }

   test("test 1 should run TestCaseExtension concurrently") {
      "void"
   }

   test("test 2 should run TestCaseExtension concurrently") {
      "void"
   }

   test("test 3 should run TestCaseExtension concurrently") {
      "void"
   }
})

private val aroundSpecCounter = AtomicInteger(0)
private val lockedCounterSpecExtensionConcurrent = AtomicInteger(0)

class SpecThreadSpecExtensionConcurrentSingleInstanceTest : FunSpec({

   isolationMode = IsolationMode.SingleInstance
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
