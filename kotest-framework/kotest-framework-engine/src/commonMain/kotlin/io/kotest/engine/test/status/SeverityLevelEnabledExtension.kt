package io.kotest.engine.test.status

import io.kotest.core.log
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver

/**
 * This [TestEnabledExtension] disables tests if a test's [TestCaseSeverityLevel] is lower than
 * the runtime minimum severity level.
 *
 * If a test case does not specify a level, then it defaults to [TestCaseSeverityLevel.NORMAL].
 *
 * Note: If no minimum severity level is specified, then this extension has no effect.
 */
internal class SeverityLevelEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
   private val testConfigResolver: TestConfigResolver,
) :
   TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {

      // if min level is not defined, then we always allow through
      val minLevel = projectConfigResolver.minimumRuntimeTestSeverityLevel()
      val testLevel = testConfigResolver.severity(testCase)

      return when {
         minLevel == null -> Enabled.enabled
         testLevel.level >= minLevel.level -> Enabled.enabled
         else -> Enabled
            .disabled("${testCase.descriptor.path()} is disabled by severity level (minimum level is $minLevel)")
            .also { it.reason?.let { log { it } } }
      }
   }
}
