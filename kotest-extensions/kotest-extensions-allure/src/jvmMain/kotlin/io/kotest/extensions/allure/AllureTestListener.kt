package io.kotest.extensions.allure

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.reflect.KClass

@Deprecated("Use AllureTestReporter(); this Will be removed in 4.6", level = DeprecationLevel.ERROR)
val AllureTestListener = AllureTestReporter()

class AllureTestReporter(private val includeContainers: Boolean = false) : TestListener, ProjectListener, SpecInstantiationListener {

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

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      writer.allureResultSpecInitFailure(kclass, t)
   }
}
