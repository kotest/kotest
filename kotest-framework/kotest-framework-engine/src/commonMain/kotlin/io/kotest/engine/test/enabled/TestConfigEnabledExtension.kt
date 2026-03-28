package io.kotest.engine.test.enabled

import io.kotest.common.syspropOrEnv
import io.kotest.core.Logger
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.TestConfigResolver

/**
 * A [TestEnabledExtension] that uses the enabled or enabledIf functions in
 * test case configs to determine if a test is enabled.
 */
internal class TestConfigEnabledExtension(private val testConfigResolver: TestConfigResolver) : TestEnabledExtension {

   private val logger = Logger<TestConfigEnabledExtension>()

   override fun isEnabled(testCase: TestCase): Enabled {
      if (syspropOrEnv(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE) == "true") return Enabled.enabled
      val enabled = testConfigResolver.enabled(testCase).invoke(testCase)
      if (enabled.isDisabled)
         logger.log { "${testCase.descriptor.path()} is disabled by enabledOrReasonIf function in config: ${enabled.reason}" }
      return enabled
   }
}
