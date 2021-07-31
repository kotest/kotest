package io.kotest.engine.listener

import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestCaseExecutionListener
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [TestCaseExecutionListener] that will buffer the state of root tests and their nested
 * tests until the [rootFinished] method is invoked.
 */
class BufferedTestCaseExcecutionListener(private val listener: TestCaseExecutionListener) : TestCaseExecutionListener {

   private val started = ConcurrentHashMap<Description, TestCase>()
   private val ignored = ConcurrentHashMap<Description, TestCase>()
   private val finished = ConcurrentHashMap<Description, Pair<TestCase, TestResult>>()
   private val mutex = Mutex()

   override suspend fun testStarted(testCase: TestCase) {
      synchronized(this) {
         started[testCase.description] = testCase
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(this) {
         finished[testCase.description] = testCase to result
      }
   }

   suspend fun rootFinished(testCase: TestCase) {
      require(testCase.description.isRootTest())
      mutex.withLock {
         startStop(testCase, finished[testCase.description]!!.second)
      }
   }

   private suspend fun startStop(testCase: TestCase, result: TestResult) {
      listener.testStarted(testCase)
      finished
         .filter { testCase.description.isParentOf(it.key) }
         .forEach { startStop(it.value.first, it.value.second) }
      ignored
         .filter { testCase.description.isParentOf(it.key) }
         .forEach { listener.testIgnored(it.value) }
      listener.testFinished(testCase, result)
   }

   override suspend fun testIgnored(testCase: TestCase) {
      synchronized(this) {
         ignored[testCase.description] = testCase
      }
   }
}
