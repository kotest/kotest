package io.kotest.core.spec.style.scopes

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

class FreeSpecTerminalContext(
   val testContext: TestContext,
) : TestContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) = error("Cannot nest a test inside a terminal context")

   // exists to stop nesting
   @Deprecated("Cannot nest leaf test inside another leaf test", level = DeprecationLevel.ERROR)
   suspend infix operator fun String.invoke(test: suspend TestContext.() -> Unit) {
   }
}
