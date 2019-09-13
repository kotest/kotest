package com.sksamuel.kotest.specs.stringspec

import currentThreadWithoutCoroutine
import io.kotest.Description
import io.kotest.TestCaseOrder
import io.kotest.TestResult
import io.kotest.matchers.string.shouldStartWith
import io.kotest.shouldBe
import io.kotest.specs.StringSpec
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class StringSpecCoroutineSingleTestIsolationModeTest : StringSpec() {

  private var longOpCompleted = false
  private val count = AtomicInteger(0)
  private val threadnames = ConcurrentHashMap.newKeySet<String>()
  private var listenerThread = ""

  override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential

  override fun beforeTest(description: Description) {
    listenerThread = currentThreadWithoutCoroutine()
  }

  override fun afterTest(description: Description, result: TestResult) {
    listenerThread shouldBe currentThreadWithoutCoroutine()
  }

  init {

    "string spec should support suspend functions" {
      longop()
      longOpCompleted shouldBe true
    }

    "string spec should support async" {
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

    "string spec with multiple invocations and suspend functions".config(invocations = 100) {
      delay(500)
      count.incrementAndGet()
    }

    "previous test result" {
      count.get() shouldBe 100
    }

    "string spec with multiple invocations and parallelism".config(invocations = 100, threads = 10) {
      delay(500)
      count.incrementAndGet()
    }

    "previous test result 2" {
      count.get() shouldBe 200
    }

    // we need enough invocation to ensure all the threads get used up
    "mutliple threads should use a thread pool for the coroutines".config(invocations = 50, threads = 6) {
      logThreadName()
    }

    "previous test result 3" {
      threadnames.size shouldBe 6
    }

    "a single threaded test should run listeners on the same thread as the test" {
      Thread.currentThread().name.shouldStartWith(listenerThread)
    }
  }

  private suspend fun longop() {
    delay(500)
    longOpCompleted = true
  }

  private fun logThreadName() {
    threadnames.add(currentThreadWithoutCoroutine())
  }
}