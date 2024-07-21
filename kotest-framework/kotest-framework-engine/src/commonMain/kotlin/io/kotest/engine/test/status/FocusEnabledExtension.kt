package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest
import io.kotest.core.log

/**
 * This [TestEnabledExtension] disables tests if the containing spec has focused tests,
 * and this test is not focused.
 *
 * Note: This extension only applies to root tests.
 * Nested tests are not affected by this extension.
 */
internal object FocusEnabledExtension : TestEnabledExtension {

   override fun isEnabled(testCase: TestCase): Enabled {

      // focus only applies to root tests
      if (!testCase.isRootTest()) return Enabled.enabled

      // if we are focused doesn't matter what anyone else does
      if (testCase.name.focus) return Enabled.enabled

      // if anything else is focused we're outta luck
      if (testCase.spec.rootTests().any { it.name.focus }) {
         return Enabled
            .disabled("${testCase.descriptor.path().value} is disabled by another test having focus")
            .also { it.reason?.let { log { it } } }
      }

      return Enabled.enabled
   }
}
