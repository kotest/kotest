package io.kotest.engine.listener

import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [TestCaseExecutionListener] that will buffer the state of root tests and their nested
 * tests until the [rootFinished] method is invoked.
 */
class BufferedTestCaseExcecutionListener(private val listener: TestCaseExecutionListener) : TestCaseExecutionListener {

   private val started = ConcurrentHashMap<Description, TestCase>()
   private val ignored = ConcurrentHashMap<Description, TestCase>()
   private val finished = ConcurrentHashMap<Description, Pair<TestCase, TestResult>>()

   override fun testStarted(testCase: TestCase) {
      synchronized(this) {
         started[testCase.description] = testCase
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(this) {
         finished[testCase.description] = testCase to result
      }
   }

   fun rootFinished(testCase: TestCase) {
      require(testCase.isTopLevel())
      synchronized(this) {
         startStop(testCase, finished[testCase.description]!!.second)
      }
   }

   private fun startStop(testCase: TestCase, result: TestResult) {
      listener.testStarted(testCase)
      finished
         .filter { testCase.description.isParentOf(it.key) }
         .forEach { startStop(it.value.first, it.value.second) }
      ignored
         .filter { testCase.description.isParentOf(it.key) }
         .forEach { listener.testIgnored(it.value) }
      listener.testFinished(testCase, result)
   }

   override fun testIgnored(testCase: TestCase) {
      synchronized(this) {
         ignored[testCase.description] = testCase
      }
   }
}
