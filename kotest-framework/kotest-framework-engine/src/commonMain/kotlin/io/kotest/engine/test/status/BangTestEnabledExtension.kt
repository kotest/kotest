package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.log
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.common.sysprop

/**
 * A [TestEnabledExtension] that disabled a test if the name of the test is prefixed with "!"
 * and System.getProperty("kotest.bang.disable") has a null value (ie, not defined)
 */
internal object BangTestEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {

      // this sys property disables the use of !
      // when it is true, we don't check for !
      if (sysprop(KotestEngineProperties.DISABLE_BANG_PREFIX) == "true") {
         return Enabled.enabled
      }

      if (testCase.name.bang) {
         return Enabled
            .disabled("Disabled by bang")
            .also { it.reason?.let { log { it } } }
      }

      return Enabled.enabled
   }
}
