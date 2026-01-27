package io.kotest.runner.junit4

import io.kotest.common.reflection.bestName
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import org.junit.runner.Description
import kotlin.reflect.KClass

@Deprecated("Use DescriptionHandler. This will be removed in 7.0")
fun describeTestCase(testCase: TestCase, displayName: String): Description =
   Description.createTestDescription(
      testCase.spec::class.bestName(),
      displayName
   )

internal object Descriptions {

   private val formatter = DefaultDisplayNameFormatter(ProjectConfigResolver(), TestConfigResolver())

   /**
    * Returns a JUnit4 [Description] for a [TestCase].
    *
    * Since JUnit4 doesn't handle nested tests, we'll add the full context path to the test name.
    */
   fun createTestDescription(testCase: TestCase): Description {
      return Description.createTestDescription(testCase.spec::class.java, testPath(testCase))
   }

   private fun testPath(testCase: TestCase): String {
      val name = escapeInstrumentedTestName(formatter.format(testCase))
      return when (val parent = testCase.parent) {
         null -> name
         else -> testPath(parent) + " " + '\u21E2' + " " + name
      }
   }

   /**
    * Returns a JUnit4 [Description] to be used as a placeholder for a failed spec.
    */
   fun createPlaceholderErrorDescription(clazz: KClass<*>, e: Throwable): Description {
      return Description.createTestDescription(clazz.java, e::class.java.simpleName)
   }

   /**
    * Returns a test name which has been escaped for Android instrumented tests.
    *
    * Known Bug: As of mid-2024, versions like Orchestrator 1.5.0 have a reported issue where whitespaces in
    * test function names lead to an IllegalStateException. This occurs because the orchestrator fails to
    * properly encode shell parameters for the test run.
    *
    * See https://github.com/android/android-test/issues/2255
    */
   internal fun escapeInstrumentedTestName(name: String) = name.replace('/', ' ')

}
