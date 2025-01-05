package io.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.timeout
import kotlin.time.Duration

/**
 * The [ProjectConfigResolver] is responsible for returning the runtime value to use for a given
 * configuration setting based on the various sources of configuration.
 *
 * For example, a test name case can be specified
 */
class ProjectConfigResolver(
   private val configuration: ProjectConfiguration,
) {

   fun timeout(testCase: TestCase): Duration {
      return testCase.timeout
   }

   fun testSeverity() {
      return configuration.testSeverity
   }

   /**
    * Returns the [TestCaseOrder] applicable for this spec.
    *
    * If the spec has a [TestCaseOrder] set, either directly or via a shared default test config,
    * then that is used, otherwise the project default is used.
    */
   fun testCaseOrder(spec: Spec): TestCaseOrder {
      return spec.testCaseOrder() ?: spec.testOrder ?: spec.defaultTestConfig?.testOrder ?: configuration.testCaseOrder
   }

   /**
    * Returns true if the test style affixes should be included in the test name.
    * For example, some spec styles add prefixes or suffixes to the test name, and this
    * setting specifies if those should be included in the displayed test name.
    */
   fun includeTestScopeAffixes(testCase: TestCase): Boolean {
      return configuration.includeTestScopeAffixes ?: testCase.name.defaultAffixes
   }

   /**
    * Returns the [TestNameCase] to use when outputing test names.
    * This setting is only settable at the global level.
    */
   fun testNameCase(): TestNameCase {
      return configuration.testNameCase
   }

   /**
    * Returns true if tags specified on a test should be included in the test name output.
    */
   fun testNameAppendTags(): Boolean {
      return configuration.testNameAppendTags
   }

   /**
    * Returns true if the test name should be the full name including parent names.
    */
   fun displayFullTestPath(): Boolean {
      return configuration.displayFullTestPath
   }
}
