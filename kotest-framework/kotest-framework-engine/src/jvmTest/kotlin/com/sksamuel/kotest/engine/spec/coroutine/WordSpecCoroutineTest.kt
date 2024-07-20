package com.sksamuel.kotest.engine.spec.coroutine

import com.sksamuel.kotest.engine.coroutines.provokeThreadSwitch
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class WordSpecCoroutineTest : WordSpec() {

   private var longOpCompleted = false
   private val count = AtomicInteger(0)
   private val threadnames = ConcurrentHashMap.newKeySet<String>()
   private var listenerThread = ""

   override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential

   override suspend fun beforeTest(testCase: TestCase) {
      listenerThread = currentThreadWithoutCoroutine()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      listenerThread shouldBe currentThreadWithoutCoroutine()
   }

   init {

      "word spec" should {

         "support suspend functions" {
            longop()
            longOpCompleted shouldBe true
         }

         "support async" {
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

         "multiple invocations".config(invocations = 20) {
            delay(5)
            count.incrementAndGet()
         }

         "previous test result" {
            count.get() shouldBe 20
         }

         "multiple invocations and parallelism".config(invocations = 20, threads = 10) {
            count.incrementAndGet()
            provokeThreadSwitch()
         }

         "previous test result 2" {
            count.get() shouldBe 40
         }

         // we need enough invocations to ensure multiple threads get used up
         "mutliple threads should use a thread pool for the coroutines".config(
            invocations = 6,
            threads = 6
         ) {
            logThreadName()
            provokeThreadSwitch()
         }

         "previous test result 3" {
            threadnames.size shouldBeGreaterThan 1
         }

         "run listeners on the same thread as the test when a single invocation" {
            Thread.currentThread().name.shouldStartWith(listenerThread)
         }
      }
   }

   private suspend fun longop() {
      delay(5)
      longOpCompleted = true
   }

   private fun logThreadName() {
      threadnames.add(currentThreadWithoutCoroutine())
   }
}
