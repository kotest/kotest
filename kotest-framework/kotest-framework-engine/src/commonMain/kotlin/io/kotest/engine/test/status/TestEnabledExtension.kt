package io.kotest.engine.test.status

import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase

/**
 * An internal extension that is used to determine if a test is enabled or disabled.
 *
 * Any extension can choose to disable a test - all extensions must reply enabled
 * for a test to be considered active.
 */
internal interface TestEnabledExtension {
   fun isEnabled(testCase: TestCase): Enabled
}
