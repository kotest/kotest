package com.sksamuel.kotest.core.runtime.parallel

import com.sksamuel.kotest.core.runtime.PersistentThreadLocal
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.getOrSet

class SpecThreadSingleInstanceTest : FunSpec({

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

   val multipleThreadMultipleInvocationCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      multipleThreadMultipleInvocationCounter.map.shouldHaveSize(3)
      multipleThreadMultipleInvocationCounter.map.values.sum() shouldBe 3
   }

   test("test 1 should create own key in map with value 1") {
      val counter = multipleThreadMultipleInvocationCounter.getOrSet { 0 }
      multipleThreadMultipleInvocationCounter.set(counter + 1)
   }

   test("test 2 should create own key in map with value 1") {
      val counter = multipleThreadMultipleInvocationCounter.getOrSet { 0 }
      multipleThreadMultipleInvocationCounter.set(counter + 1)
   }

   test("test 3 should create own key in map with value 1") {
      val counter = multipleThreadMultipleInvocationCounter.getOrSet { 0 }
      multipleThreadMultipleInvocationCounter.set(counter + 1)
   }

   val lock = ReentrantLock()

   test("test should lock object") {
      lock.lock()
      delay(1000)
      lock.unlock()
   }

   test("lock should be locked") {
      delay(300)
      lock.isLocked shouldBe true
   }

   test("lock should be locked too") {
      delay(300)
      shouldThrow<AssertionError> {
         lock.isLocked shouldBe false
      }
   }
})

class SpecThreadWithNestedTestSingleInstanceTest : FunSpec({

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

   val multipleThreadCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      assertSoftly {
         multipleThreadCounter.map.shouldHaveSize(2)
         multipleThreadCounter.map.values.sum() shouldBe 4
      }

   }

   context("First single thread context") {
      test("test 1 should create new key in map for context or add value 1") {
         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }

      test("test 2 should create new key in map for context or add value 1") {
         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }
   }

   context("Second single thread context") {
      test("test 1 should create new key in map for context or add value 1") {
         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }

      test("test 2 should create new key in map for context or add value 1") {
         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }
   }

   val lock = ReentrantLock()

   context("Third single thread context") {

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
   }


})

private val beforeTestCounter = AtomicInteger(0)
private val afterTestCounter = AtomicInteger(0)
private val beforeSpecCounter = AtomicInteger(0)
private val afterSpecCounter = AtomicInteger(0)

class SpecThreadBeforeAfterSingleInstanceTest : FunSpec({

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

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

private val lockedCounter = AtomicInteger(0)
private val counterBeforeTestConcurrent = AtomicInteger(0)

class SpecThreadBeforeTestConcurrentSingleInstanceTest : FunSpec({

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

   val lock = ReentrantLock()

   beforeTest {
      while (lock.isLocked) {
         lockedCounter.getAndIncrement()
      }
      lock.lock()
      delay(300)
      lock.unlock()
      counterBeforeTestConcurrent.getAndIncrement()
   }

   afterProject {
      lockedCounter.get() shouldBeGreaterThan 0
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

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

   val lock = ReentrantLock()

   extension { (testCase, execute) ->
      while (lock.isLocked) {
         lockedCounterTestExtensionConcurrent.getAndIncrement()
      }
      lock.lock()
      delay(300)
      lock.unlock()
      counterTestExtensionConcurrent.getAndIncrement()
      execute(testCase)
   }

   afterProject {
      lockedCounterTestExtensionConcurrent.get() shouldBeGreaterThan 0
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

   isolation = IsolationMode.SingleInstance
   threadsForSpec = 3

   val lock = ReentrantLock()

   aroundSpec { (spec, process) ->
      while (lock.isLocked) {
         lockedCounterSpecExtensionConcurrent.getAndIncrement()
      }
      lock.lock()
      delay(300)
      lock.unlock()

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
