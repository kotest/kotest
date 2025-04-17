package io.kotest.engine.test.status

import io.kotest.core.log
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.TestConfigResolver

/**
 * A [TestEnabledExtension] that uses the enabled or enabledIf functions in
 * test case configs to determine if a test is enabled.
 */
internal class TestConfigEnabledExtension(private val testConfigResolver: TestConfigResolver) : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      val enabled = testConfigResolver.enabled(testCase).invoke(testCase)
      if (enabled.isDisabled)
         log { "${testCase.descriptor.path()} is disabled by enabledOrReasonIf function in config: ${enabled.reason}" }
      return enabled
   }
}
