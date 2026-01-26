package io.kotest.runner.junit4

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.test.TestResult
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import kotlin.reflect.KClass

/**
 * Some observations:
 *
 * Intermediate Suites are not output in intellij or Gradle, only the top level suite from the class and leaf tests.
 * In Gradle the display name for tests is the "class name" + the "test name".
 * In IntelliJ the display name for tests is simply the test name.
 */
internal class JUnitTestEngineListener(
   private val notifier: RunNotifier,
) : AbstractTestEngineListener() {

   // the runner takes care of spec started/finished from the description that is returned from the JUnit4 Runner
   override suspend fun specStarted(ref: SpecRef) {}

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      // there's no ignored option for suites, so we have to add it as a test
      notifier.fireTestIgnored(Description.createTestDescription(kclass.java.name, kclass.java.simpleName))
   }

   // the runner takes care of spec started/finished from the description that is returned from the JUnit4 Runner
   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      // since JUnit4 has no support for suite level errors, if the spec had an error, we'll add a placeholder test
      // and mark that as failed so the user can see the issue
      val e = result.errorOrNull
      if (e != null) {
         val d = Descriptions.createPlaceholderErrorDescription(ref.kclass, e)
         notifier.fireTestStarted(d)
         notifier.fireTestFailure(Failure(d, e))
         notifier.fireTestFinished(d)
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      val desc = Descriptions.createTestDescription(testCase)
      notifier.fireTestStarted(desc)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      val desc = Descriptions.createTestDescription(testCase)
      when (result) {
         is TestResult.Success -> notifier.fireTestFinished(desc)
         is TestResult.Error -> notifyFailure(desc, result)
         is TestResult.Ignored -> notifier.fireTestIgnored(desc)
         is TestResult.Failure -> notifyFailure(desc, result)
      }
   }

   private fun notifyFailure(desc: Description, result: TestResult) {
      notifier.fireTestFailure(Failure(desc, result.errorOrNull))
      notifier.fireTestFinished(desc)
   }
}
