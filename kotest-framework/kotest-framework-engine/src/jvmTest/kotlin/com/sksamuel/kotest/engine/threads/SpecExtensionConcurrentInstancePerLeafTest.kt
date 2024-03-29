//@file:Suppress("unused", "NAME_SHADOWING")
//
//package com.sksamuel.kotest.core.runtime.parallel
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.ints.shouldBeExactly
//import kotlinx.coroutines.delay
//import java.util.concurrent.atomic.AtomicInteger
//import java.util.concurrent.locks.ReentrantLock
//
//private val externalMultipleThreadCounter = PersistentThreadLocal<Int>()
//private val externalThreadAccum = PersistentThreadLocal<String>()
//
//
//
//
//
//private val lockedCounter = AtomicInteger(0)
//private val counterBeforeTestConcurrent = AtomicInteger(0)
//
//
//private val aroundSpecCounter = AtomicInteger(0)
//private val lockedCounterSpecExtensionConcurrent = AtomicInteger(0)
//
//class SpecThreadSpecExtensionConcurrentInstancePerLeafTest : FunSpec({
//
//   isolationMode = IsolationMode.InstancePerLeaf
//   threads = 3
//
//
//   val lock = ReentrantLock()
//
//   aroundSpec { (_, process) ->
//      val isLockAcquired = lock.tryLock()
//      if (isLockAcquired) {
//         lock.lock()
//         try {
//            delay(300)
//         } finally {
//            lock.unlock()
//         }
//      } else {
//         lockedCounterSpecExtensionConcurrent.getAndIncrement()
//      }
//      aroundSpecCounter.getAndIncrement()
//      process()
//   }
//
//   afterProject {
//      lockedCounterSpecExtensionConcurrent.get() shouldBeExactly 0
//      aroundSpecCounter.get() shouldBeExactly 1
//   }
//
//   test("test 1 aroundSpecExtension should be called only once for entire Spec without concurrency") {
//      "void"
//   }
//
//   test("test 2 aroundSpecExtension should be called only once for entire Spec without concurrency") {
//      "void"
//   }
//
//   test("test 3 aroundSpecExtension should be called only once for entire Spec without concurrency") {
//      "void"
//   }
//})
//
