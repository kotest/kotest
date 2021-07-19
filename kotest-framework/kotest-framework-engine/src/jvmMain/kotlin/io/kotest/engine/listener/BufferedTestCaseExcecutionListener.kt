package io.kotest.engine.listener

import io.kotest.core.plan.TestPath
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestCaseExecutionListener
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [TestCaseExecutionListener] that will buffer the state of root tests and their nested
 * tests until the [rootFinished] method is invoked.
 */
class BufferedTestCaseExcecutionListener(private val listener: TestCaseExecutionListener) : TestCaseExecutionListener {

   private val started = ConcurrentHashMap<TestPath, TestCase>()
   private val ignored = ConcurrentHashMap<TestPath, TestCase>()
   private val finished = ConcurrentHashMap<TestPath, Pair<TestCase, TestResult>>()

   override fun testStarted(testCase: TestCase) {
      synchronized(this) {
         started[testCase.descriptor.testPath()] = testCase
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(this) {
         finished[testCase.descriptor.testPath()] = testCase to result
      }
   }

   fun rootFinished(testCase: TestCase) {
      require(testCase.descriptor.isTopLevel())
      synchronized(this) {
         startStop(testCase, finished[testCase.descriptor.testPath()]!!.second)
      }
   }

   private fun startStop(testCase: TestCase, result: TestResult) {
      listener.testStarted(testCase)
      finished
         .filter { testCase.descriptor.testPath().isAncestorOf(it.key) }
         .forEach { startStop(it.value.first, it.value.second) }
      ignored
         .filter { testCase.descriptor.testPath().isAncestorOf(it.key) }
         .forEach { listener.testIgnored(it.value) }
      listener.testFinished(testCase, result)
   }

   override fun testIgnored(testCase: TestCase) {
      synchronized(this) {
         ignored[testCase.descriptor.testPath()] = testCase
      }
   }
}
