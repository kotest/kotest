package io.kotest.engine.test.registration

import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestResult

object TerminalRegistration : Registration {
   override suspend fun registerNestedTest(nested: NestedTest): TestResult? {
      error("Not supported")
   }
}

object NoopRegistration : Registration {
   override suspend fun registerNestedTest(nested: NestedTest): TestResult? = null
}
