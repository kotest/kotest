package io.kotest.engine.test.status

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.isBang
import io.kotest.mpp.log
import io.kotest.mpp.sysprop

/**
 * A [TestEnabledExtension] that disabled a test if the name of the test is prefixed with "!"
 * and System.getProperty("kotest.bang.disable") has a null value (ie, not defined)
 */
object BangTestEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      // this sys property disables the use of !
      // when it's not set, then we use ! to disable tests
      val bangEnabled = sysprop(KotestEngineProperties.disableBangPrefix) == null
      if (testCase.isBang() && bangEnabled) {
         return Enabled.disabled("${testCase.description.testPath()} is disabled by bang")
            .also { log { it.reason } }
      }

      return Enabled.enabled
   }
}
