//package com.sksamuel.kotest.engine.threads
//
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.collections.shouldHaveSize
//import io.kotest.matchers.shouldBe
//import kotlinx.coroutines.delay
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.locks.ReentrantLock
//
//private val objects = ConcurrentHashMap.newKeySet<ReentrantLock>()
//
//class SpecThreadSingleInstanceWithLockTest : FunSpec({
//
//   isolationMode = IsolationMode.SingleInstance
//   threads = 3
//
//   val lock = ReentrantLock()
//
//   afterProject {
//      //The same object
//      objects shouldHaveSize 1
//   }
//
//   test("test should lock object") {
//      objects.add(lock)
//      lock.lock()
//      try {
//         delay(1000)
//      } finally {
//         lock.unlock()
//      }
//
//   }
//
//   test("lock should be locked") {
//      objects.add(lock)
//      delay(300)
//      lock.isLocked shouldBe true
//   }
//
//   test("lock should be locked too") {
//      objects.add(lock)
//      delay(300)
//      shouldThrow<AssertionError> {
//         lock.isLocked shouldBe false
//      }
//   }
//
//})
