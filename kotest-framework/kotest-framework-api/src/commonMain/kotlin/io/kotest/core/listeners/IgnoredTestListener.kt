package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase

/**
 * Is notified when a test is ignored.
 */
interface IgnoredTestListener : Extension {
   /**
    * Invoked whenever [testCase] is ignored by [reason].
    */
   suspend fun ignoredTest(testCase: TestCase, reason: String?): Unit = Unit
}
