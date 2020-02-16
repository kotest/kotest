//package com.sksamuel.kotest.specs.feature
//
//import currentThreadWithoutCoroutine
//import io.kotest.core.test.Description
//import io.kotest.core.test.TestResult
//import io.kotest.matchers.string.shouldStartWith
//import io.kotest.shouldBe
//import io.kotest.specs.FeatureSpec
//import kotlinx.coroutines.async
//import kotlinx.coroutines.delay
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//class FeatureSpecCoroutineSingleTestIsolationModeTest : FeatureSpec() {
//
//  private var longOpCompleted = false
//  private val count = AtomicInteger(0)
//  private val threadnames = ConcurrentHashMap.newKeySet<String>()
//  private var listenerThread = ""
//
//  override fun beforeTest(description: Description) {
//    // strip off the coroutine suffix
//    listenerThread = currentThreadWithoutCoroutine()
//  }
//
//  override fun afterTest(description: Description, result: TestResult) {
//    // strip off the coroutine suffix
//    listenerThread shouldBe currentThreadWithoutCoroutine()
//  }
//
//  init {
//
//    feature("feature spec coroutine support") {
//      scenario("should support suspend functions") {
//        longop()
//        longOpCompleted shouldBe true
//      }
//      scenario("should support async") {
//        val counter = AtomicInteger(0)
//        val a = async {
//          counter.incrementAndGet()
//        }
//        val b = async {
//          counter.incrementAndGet()
//        }
//        a.await()
//        b.await()
//        counter.get() shouldBe 2
//      }
//      scenario("multiple invocations").config(invocations = 100) {
//        delay(500)
//        count.incrementAndGet()
//      }
//      scenario("previous test result") {
//        count.get() shouldBe 100
//      }
//      scenario("multiple invocations and parallelism").config(invocations = 100, threads = 10) {
//        delay(500)
//        count.incrementAndGet()
//      }
//      scenario("previous test result 2") {
//        count.get() shouldBe 200
//      }
//      // we need enough invocation to ensure all the threads get used up
//      scenario("mutliple threads should use a thread pool for the coroutines").config(invocations = 50, threads = 6) {
//        logThreadName()
//      }
//      scenario("previous test result 3") {
//        threadnames.size shouldBe  6
//      }
//      scenario("a single threaded test should run listeners on the same thread as the test") {
//        Thread.currentThread().name.shouldStartWith(listenerThread)
//      }
//    }
//  }
//
//  private suspend fun longop() {
//    delay(500)
//    longOpCompleted = true
//  }
//
//  private suspend fun logThreadName() {
//    delay(10)
//    Thread.sleep(10)
//    // strip off the coroutine suffix
//    threadnames.add(currentThreadWithoutCoroutine())
//  }
//}
