package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.test.names.isFocused
import io.kotest.mpp.log

/**
 * This [TestEnabledExtension] disables tests if the containing spec has focused tests, and this
 * test is not focused.
 *
 * Note: This extension only applies to root tests.
 * Nested tests are not affected by this extension.
 */
object FocusEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {

      if (!testCase.descriptor.isTopLevel()) return Enabled.enabled

      val hasFocusedTests = testCase.spec.testNames().any { it.focus }

      if (hasFocusedTests && !testCase.isFocused()) {
         return Enabled.disabled("${testCase.descriptor.name} is disabled by another top level test having focus")
            .also { log { it.reason } }
      }

      return Enabled.enabled
   }
}
