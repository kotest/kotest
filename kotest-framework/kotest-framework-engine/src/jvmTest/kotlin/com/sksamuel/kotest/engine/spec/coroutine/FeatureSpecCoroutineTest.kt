package com.sksamuel.kotest.engine.spec.coroutine

import com.sksamuel.kotest.engine.coroutines.provokeThreadSwitch
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecCoroutineTest : FeatureSpec() {

   private var longOpCompleted = false
   private val count = AtomicInteger(0)
   private val threadnames = ConcurrentHashMap.newKeySet<String>()
   private var listenerThread = ""

   override suspend fun beforeTest(testCase: TestCase) {
      // strip off the coroutine suffix
      listenerThread = currentThreadWithoutCoroutine()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      // strip off the coroutine suffix
      listenerThread shouldBe currentThreadWithoutCoroutine()
   }

   init {

      feature("feature spec coroutine support") {
         scenario("should support suspend functions") {
            longop()
            longOpCompleted shouldBe true
         }
         scenario("should support async") {
            val counter = AtomicInteger(0)
            val a = async {
               counter.incrementAndGet()
            }
            val b = async {
               counter.incrementAndGet()
            }
            a.await()
            b.await()
            counter.get() shouldBe 2
         }
         scenario("multiple invocations").config(invocations = 20) {
            delay(5)
            count.incrementAndGet()
         }
         scenario("previous test result") {
            count.get() shouldBe 20
         }
         scenario("multiple invocations and parallelism").config(invocations = 20, threads = 10) {
            count.incrementAndGet()
            provokeThreadSwitch()
         }
         scenario("previous test result 2") {
            count.get() shouldBe 40
         }
         // we need enough invocations to ensure multiple threads get used up
         scenario("multiple threads should use a thread pool for the coroutines").config(
            invocations = 6,
            threads = 6
         ) {
            // strip off the coroutine suffix
            threadnames.add(currentThreadWithoutCoroutine())
            provokeThreadSwitch()
         }
         scenario("previous test result 3") {
            threadnames.size shouldBeGreaterThan 1
         }
         scenario("a single threaded test should run listeners on the same thread as the test") {
            Thread.currentThread().name.shouldStartWith(listenerThread)
         }
      }
   }

   private suspend fun longop() {
      delay(500)
      longOpCompleted = true
   }
}
