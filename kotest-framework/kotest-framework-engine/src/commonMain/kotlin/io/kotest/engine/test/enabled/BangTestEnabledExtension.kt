package io.kotest.engine.test.enabled

import io.kotest.common.sysprop
import io.kotest.core.Logger
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.KotestEngineProperties

/**
 * A [TestEnabledExtension] that disabled a test if the name of the test is prefixed with "!"
 * and System.getProperty("kotest.bang.disable") has a null value (ie, not defined)
 */
internal object BangTestEnabledExtension : TestEnabledExtension {

   private val logger = Logger<BangTestEnabledExtension>()

   override fun isEnabled(testCase: TestCase): Enabled {

      // this sys property disables the use of !
      // when it is true, we don't check for the bang
      if (sysprop(KotestEngineProperties.DISABLE_BANG_PREFIX) == "true") {
         return Enabled.enabled
      }

      if (testCase.name.bang) {
         return Enabled
            .disabled("Disabled by bang")
            .also { enabled -> enabled.reason?.let { logger.log { it } } }
      }

      return Enabled.enabled
   }
}
