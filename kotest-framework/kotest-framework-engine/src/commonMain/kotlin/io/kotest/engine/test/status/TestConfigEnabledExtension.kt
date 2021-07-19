package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.mpp.log

/**
 * A [TestEnabledExtension] that uses the enabled / enabledIf values in test case config
 * to determine if a test is enabled.
 *
 * This extension disables a test if:
 *
 * - The `enabledOrReasonIf` function evaluates to [Enabled.disabled] in the [TestCaseConfig] associated with the test.
 * - The `enabled` property is set to false in the [TestCaseConfig] associated with the test.
 * - The `enabledIf` function evaluates to [false] in the [TestCaseConfig] associated with the test.
 */
object TestConfigEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {

      val enabledOrReasonIf = testCase.config.enabledOrReasonIf(testCase)
      if (!enabledOrReasonIf.isEnabled) {
          log { "${testCase.descriptor.name.testName} is disabled by enabledOrReasonIf function in config: ${enabledOrReasonIf.reason}" }
         return enabledOrReasonIf
      }

      if (!testCase.config.enabled) {
         return Enabled.disabled("${testCase.descriptor.name.testName} is disabled by enabled property in config")
            .also { log { it.reason } }
      }

      if (!testCase.config.enabledIf(testCase)) {
         return Enabled.disabled("${testCase.descriptor.name.testName} is disabled by enabledIf function in config")
            .also { log { it.reason } }
      }

      return Enabled.enabled
   }
}
