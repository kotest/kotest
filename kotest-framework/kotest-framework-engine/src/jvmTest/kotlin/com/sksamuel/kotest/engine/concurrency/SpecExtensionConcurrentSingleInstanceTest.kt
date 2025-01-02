@file:Suppress("unused", "NAME_SHADOWING")

package com.sksamuel.kotest.engine.concurrency

import java.util.concurrent.atomic.AtomicInteger


private val aroundSpecCounter = AtomicInteger(0)
private val lockedCounterSpecExtensionConcurrent = AtomicInteger(0)

//class SpecThreadSpecExtensionConcurrentSingleInstanceTest : FunSpec({
//
//   isolationMode = IsolationMode.SingleInstance
//   threads = 3
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
//   }
//
//   test("test 2 aroundSpecExtension should be called only once for entire Spec without concurrency") {
//   }
//
//   test("test 3 aroundSpecExtension should be called only once for entire Spec without concurrency") {
//   }
//})
