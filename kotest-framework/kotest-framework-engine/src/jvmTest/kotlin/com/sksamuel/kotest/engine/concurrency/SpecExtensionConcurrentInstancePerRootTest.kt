//package com.sksamuel.kotest.engine.concurrency
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.engine.concurrency.TestExecutionMode
//import io.kotest.matchers.ints.shouldBeExactly
//import kotlinx.coroutines.delay
//import java.util.concurrent.atomic.AtomicInteger
//import java.util.concurrent.locks.ReentrantLock
//
//private val aroundSpecCounter = AtomicInteger(0)
//private val lockedCounterSpecExtensionConcurrent = AtomicInteger(0)
//
//class SpecThreadSpecExtensionConcurrentInstancePerRootTest : FunSpec({
//
//   isolationMode = IsolationMode.InstancePerRoot
//   testExecutionMode = TestExecutionMode.Concurrent
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
//
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
