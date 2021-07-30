package io.kotest.engine

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] for root level which restricts nested tests.
 */
class TerminalTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestContext {

   init {
      require(testCase.parent == null) { "Only root level tests can be registered" }
   }

   override suspend fun registerTestCase(nested: NestedTest) {
      throw IllegalStateException("Spec styles that support nested tests are disabled in kotest-js because the underlying JS frameworks do not support promises for outer root scopes. Please use FunSpec, StringSpec, or ShouldSpec and ensure that nested contexts are not used.")
   }
}
