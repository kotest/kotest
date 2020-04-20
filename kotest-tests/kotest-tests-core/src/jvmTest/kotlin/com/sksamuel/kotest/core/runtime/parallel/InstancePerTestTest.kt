package com.sksamuel.kotest.core.runtime.parallel

import com.sksamuel.kotest.core.runtime.PersistentThreadLocal
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeInRange
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

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

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

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

   val lock = ReentrantLock()

   afterProject {
      //Different objects - for each thread each own lock
      objects shouldHaveSize 3
   }

   test("test should lock object") {
      objects.add(lock)
      lock.lock()
      delay(1000)
      lock.unlock()
   }

   test("lock should be unlocked") {
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

private val externalThreadCounter =
   PersistentThreadLocal<Int>()

class SpecThreadWithNestedTestInstancePerTestTest : FunSpec({

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

   val multipleThreadCounter =
      PersistentThreadLocal<Int>()

   afterSpec {
      assertSoftly {
         multipleThreadCounter.map shouldHaveSize 1
         multipleThreadCounter.map.values.sum() shouldBeInRange IntRange(1, 2)
      }
   }

   afterProject {
      assertSoftly {
         externalThreadCounter.map.shouldHaveSize(2)
         externalThreadCounter.map.values.sum() shouldBe 10
      }
   }

   context("First single thread context") {
      val externalCounter = externalThreadCounter.getOrSet { 0 }
      externalThreadCounter.set(externalCounter + 1)

      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      test("test 1 should create own key in map with value 1") {
         val externalCounter = externalThreadCounter.getOrSet { 0 }
         externalThreadCounter.set(externalCounter + 1)

         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }

      test("test 2 should create own key in map with value 1") {
         val externalCounter = externalThreadCounter.getOrSet { 0 }
         externalThreadCounter.set(externalCounter + 1)

         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }
   }

   context("Second single thread context") {

      val externalCounter = externalThreadCounter.getOrSet { 0 }
      externalThreadCounter.set(externalCounter + 1)

      val counter = multipleThreadCounter.getOrSet { 0 }
      multipleThreadCounter.set(counter + 1)

      test("test 1 should create key in map or add value 1") {
         val externalCounter = externalThreadCounter.getOrSet { 0 }
         externalThreadCounter.set(externalCounter + 1)

         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }

      test("test 2 should create key in map or add value 1") {
         val externalCounter = externalThreadCounter.getOrSet { 0 }
         externalThreadCounter.set(externalCounter + 1)

         val counter = multipleThreadCounter.getOrSet { 0 }
         multipleThreadCounter.set(counter + 1)
      }
   }

})

class SpecThreadWithNestedTestWithLockInstancePerTestTest : FunSpec({

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

   val lock = ReentrantLock()

   context("Third single thread context") {

      test("test should lock object") {
         println(Thread.currentThread().name)

         lock.lock()
         delay(1000)
         lock.unlock()
      }

      test("lock should be unlocked") {
         println(Thread.currentThread().name)

         delay(300)
         lock.isLocked shouldBe false
      }

      test("lock should be unlocked too") {
         println(Thread.currentThread().name)

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

class SpecThreadBeforeAfterInstancePerTestTest : FunSpec({

   isolation = IsolationMode.InstancePerTest
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

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

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
         beforeSpecNestedCounter.get() shouldBe 6
         afterSpecNestedCounter.get() shouldBe 6
         beforeTestNestedCounter.get() shouldBe 10
         afterTestNestedCounter.get() shouldBe 10
      }
   }

   context("First single thread context") {

      test("test 1 should create own key in map with value 1") {
         "void"
      }

      test("test 2 should create own key in map with value 1") {
         "void"
      }
   }

   context("Second single thread context") {

      test("test 1 should create key in map or add value 1") {
         "void"
      }

      test("test 2 should create key in map or add value 1") {
         "void"
      }
   }
})

private val lockedCounter = AtomicInteger(0)

class SpecThreadBeforeTestConcurrentInstancePerTestTest : FunSpec({

   isolation = IsolationMode.InstancePerTest
   threadsForSpec = 3

   val lock = ReentrantLock()

   beforeTest {
      while (lock.isLocked) {
         lockedCounter.getAndIncrement()
      }
      lock.lock()
      delay(300)
      lock.unlock()
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

   isolation = IsolationMode.InstancePerTest
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

   isolation = IsolationMode.InstancePerTest
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

