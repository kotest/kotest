package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase

/**
 * Is notified when a test is disabled.
 */
interface DisabledTestListener : Extension {
   /**
    * Invoked whenever [testCase] is disabled by [reason].
    */
   suspend fun disabledTest(testCase: TestCase, reason: String?): Unit = Unit
}
