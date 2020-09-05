package io.kotest.extensions.allure

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

@Deprecated("Use AllureTestReporter(); this will be removed in 4.4")
val AllureTestListener = AllureTestReporter()

class AllureTestReporter(private val includeContainers: Boolean = false) : TestListener, ProjectListener {

   override val name = "AllureTestReporter"

   val writer = AllureWriter()

   override suspend fun beforeTest(testCase: TestCase) {
      if (includeContainers || testCase.type == TestType.Test) {
         writer.startTestCase(testCase)
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (includeContainers || testCase.type == TestType.Test) {
         writer.finishTestCase(testCase, result)
      }
   }
}
