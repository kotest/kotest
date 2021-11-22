package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

@KotestTestScope
class FreeSpecTerminalScope(val testScope: TestScope) : TestScope {

   override val testCase: TestCase = testScope.testCase
   override val coroutineContext: CoroutineContext = testScope.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) = error("Cannot nest a test inside a terminal context")

   // exists to stop nesting
   @Deprecated("Cannot nest leaf test inside another leaf test", level = DeprecationLevel.ERROR)
   suspend infix operator fun String.invoke(test: suspend TestScope.() -> Unit) {
   }
}
