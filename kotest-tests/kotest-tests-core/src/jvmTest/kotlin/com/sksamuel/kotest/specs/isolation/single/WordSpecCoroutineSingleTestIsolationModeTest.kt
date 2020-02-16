//package com.sksamuel.kotest.specs.stringspec
//
//import currentThreadWithoutCoroutine
//import io.kotest.core.spec.style.WordSpec
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestCaseOrder
//import io.kotest.core.test.TestResult
//import io.kotest.matchers.string.shouldStartWith
//import io.kotest.shouldBe
//import kotlinx.coroutines.async
//import kotlinx.coroutines.delay
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//class WordSpecCoroutineSingleTestIsolationModeTest : WordSpec() {
//
//   private var longOpCompleted = false
//   private val count = AtomicInteger(0)
//   private val threadnames = ConcurrentHashMap.newKeySet<String>()
//   private var listenerThread = ""
//
//   override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential
//
//   override fun beforeTest(testCase: TestCase) {
//      listenerThread = currentThreadWithoutCoroutine()
//   }
//
//   override fun afterTest(testCase: TestCase, result: TestResult) {
//      listenerThread shouldBe currentThreadWithoutCoroutine()
//   }
//
//   init {
//
//      "word spec" should {
//         "support suspend functions" {
//            longop()
//            longOpCompleted shouldBe true
//         }
//         "support async" {
//            val counter = AtomicInteger(0)
//            val a = async {
//               counter.incrementAndGet()
//            }
//            val b = async {
//               counter.incrementAndGet()
//            }
//            a.await()
//            b.await()
//            counter.get() shouldBe 2
//         }
//
//         "previous test result" {
//            count.get() shouldBe 100
//         }
//
//         "previous test result 2" {
//            count.get() shouldBe 200
//         }
//
//         "previous test result 3" {
//            threadnames.size shouldBe 6
//         }
//
//         "run listeners on the same thread as the test when a single invocation" {
//            Thread.currentThread().name.shouldStartWith(listenerThread)
//         }
//      }
//   }
//
//   private suspend fun longop() {
//      delay(500)
//      longOpCompleted = true
//   }
//
//   private fun logThreadName() {
//      threadnames.add(currentThreadWithoutCoroutine())
//   }
//}
