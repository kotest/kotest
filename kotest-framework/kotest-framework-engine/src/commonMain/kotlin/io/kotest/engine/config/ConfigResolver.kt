package io.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.timeout
import kotlin.time.Duration

/**
 * The [ConfigResolver] is responsible for returning the actual value to use for a given configuration setting
 * at runtime based on the various sources of configuration. For example, a test timeout can be specified
 * inside the test dsl, at the spec level, at package configuration level, or globally.
 */
class ConfigResolver(private val configuration: ProjectConfiguration) {

   fun timeout(testCase: TestCase): Duration {
      return testCase.timeout
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
}
