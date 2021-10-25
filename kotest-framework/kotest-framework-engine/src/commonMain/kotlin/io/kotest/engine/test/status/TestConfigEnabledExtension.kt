package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.mpp.log

/**
 * A [TestEnabledExtension] that uses the enabled value in test case config
 * to determine if a test is enabled.
 */
internal object TestConfigEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      val enabled = testCase.config.enabled(testCase)
      return when {
         enabled.isEnabled -> Enabled.enabled
         else -> {
            log { "${testCase.descriptor.path()} is disabled by enabledOrReasonIf function in config: ${enabled.reason}" }
            return enabled
         }
      }
   }
}
