package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.TestNameCase
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel

/**
 * The [ProjectConfigResolver] is responsible for returning the runtime value to use for a given
 * configuration setting based on the various sources of configuration.
 *
 * This class handles settings that should only be configured at the project level,
 * such as spec execution order, or minimum severity level at runtime.
 *
 * For spec level equivalent, see [SpecConfigResolver].
 *
 *  Order of precedence for each setting from highest priority to lowest:
 *
 * - project level defaults from [io.kotest.core.config.ProjectConfiguration]
 * - system property overrides
 * - kotest defaults
 */
class ProjectConfigResolver(
   private val config: AbstractProjectConfig?,
) {

   private val systemPropertyConfiguration = loadSystemPropertyConfiguration()

   /**
    * Returns the minimum severity level for tests to be executed.
    */
   fun minimumRuntimeTestSeverityLevel(): TestCaseSeverityLevel? {
      return config?.minimumRuntimeTestCaseSeverityLevel
         ?: systemPropertyConfiguration.minimumRuntimeTestCaseSeverityLevel()
   }

   /**
    * Returns true if the test style affixes should be included in the test name.
    * For example, some spec styles add prefixes or suffixes to the test name, and this
    * setting specifies if those should be included in the displayed test name.
    */
   fun includeTestScopeAffixes(testCase: TestCase): Boolean {
      return config?.includeTestScopeAffixes ?: testCase.name.defaultAffixes
   }

   /**
    * Returns the [TestNameCase] to use when outputing test names.
    * This setting is only settable at the global level.
    */
   fun testNameCase(): TestNameCase {
      return config?.testNameCase ?: Defaults.TEST_NAME_CASE
   }

   /**
    * Returns true if tags specified on a test should be included in the test name output.
    */
   fun testNameAppendTags(): Boolean {
      return config?.testNameAppendTags ?: Defaults.TEST_NAME_APPEND_TAGS
   }

   /**
    * Returns true if the test name should be the full name including parent names.
    */
   fun displayFullTestPath(): Boolean {
      return config?.displayFullTestPath ?: Defaults.DISPLAY_FULL_TEST_PATH
   }
}
