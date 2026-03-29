package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.TestDefinition
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestScope

abstract class TerminalScope : TestScope {

   @Suppress("OVERRIDE_DEPRECATION")
   override suspend fun registerTestCase(nested: NestedTest) {
      error("Cannot add nested tests at this level")
   }

   override suspend fun registerTest(test: TestDefinition) {
      error("Cannot add nested tests at this level")
   }
}
