package io.kotest.engine.test.registration

import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * A [Registration] that errors if we try to register nested tests.
 */
object TerminalRegistration : Registration {
   override suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult? {
      error("This registration does not support nested tests")
   }
}
