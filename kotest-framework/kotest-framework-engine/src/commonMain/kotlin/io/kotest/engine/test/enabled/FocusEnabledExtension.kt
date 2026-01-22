package io.kotest.engine.test.enabled

import io.kotest.core.log
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest

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

      // if we are focused doesn't matter about the state of other tests
      if (testCase.name.focus || testCase.xmethod == TestXMethod.FOCUSED) return Enabled.enabled

      // if anything else is focused, we're out of luck
      if (testCase.spec.rootTests().any { it.name.focus || it.xmethod == TestXMethod.FOCUSED }) {
         return Enabled
            .disabled("${testCase.descriptor.path().value} is disabled by another test having focus")
            .also { enabled -> enabled.reason?.let { log { it } } }
      }

      return Enabled.enabled
   }
}
