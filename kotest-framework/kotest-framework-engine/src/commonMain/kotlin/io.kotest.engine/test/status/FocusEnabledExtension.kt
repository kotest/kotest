package io.kotest.engine.test.status

import io.kotest.engine.spec.focusTests
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.isFocused
import io.kotest.mpp.log

/**
 * This [TestEnabledExtension] disables tests if the containing spec has focused tests, and this
 * test is not focused.
 *
 * Note: This extension only applies to root tests. Nested tests are not affected by this extension.
 */
object FocusEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {

      if (!testCase.description.isRootTest()) return Enabled.enabled

      if (!testCase.isFocused() && testCase.spec.focusTests().isNotEmpty()) {
         return Enabled.disabled("${testCase.description.testPath()} is disabled by another test having focus")
            .also { log { it.reason } }
      }

      return Enabled.enabled
   }
}
