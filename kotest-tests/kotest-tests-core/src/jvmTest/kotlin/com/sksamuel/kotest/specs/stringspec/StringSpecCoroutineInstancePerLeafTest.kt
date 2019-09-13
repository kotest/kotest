package com.sksamuel.kotest.specs.stringspec

import currentThreadWithoutCoroutine
import io.kotest.IsolationMode
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.matchers.string.shouldStartWith
import io.kotest.shouldBe
import io.kotest.specs.StringSpec
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class StringSpecCoroutineInstancePerLeafTest : StringSpec() {

  override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

  override fun beforeTest(testCase: TestCase) {
    listenerThread = currentThreadWithoutCoroutine()
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    listenerThread shouldBe currentThreadWithoutCoroutine()
  }

  private var uniqueCount = AtomicInteger(0)

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
      uniqueCount.incrementAndGet()
      sharedCount.incrementAndGet()
    }

    "previous test result" {
      uniqueCount.get() shouldBe 0
      sharedCount.get() shouldBe 100
    }

    "string spec with multiple invocations and parallelism".config(invocations = 100, threads = 10) {
      delay(500)
      uniqueCount.incrementAndGet()
      sharedCount.incrementAndGet()
    }

    "previous test result 2" {
      uniqueCount.get() shouldBe 0
      sharedCount.get() shouldBe 200
    }

    // we need enough invocations to ensure all the threads get used up
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
    Thread.sleep(10)
    threadnames.add(currentThreadWithoutCoroutine())
  }

  companion object {
    private var longOpCompleted = false
    private val sharedCount = AtomicInteger(0)
    private val threadnames = ConcurrentHashMap.newKeySet<String>()
    private var listenerThread = ""
  }
}
