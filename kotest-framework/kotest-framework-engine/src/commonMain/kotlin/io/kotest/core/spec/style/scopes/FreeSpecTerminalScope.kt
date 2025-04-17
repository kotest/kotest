package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

@KotestTestScope
class FreeSpecTerminalScope(val testScope: TestScope) : TerminalScope() {

   override val testCase: TestCase = testScope.testCase
   override val coroutineContext: CoroutineContext = testScope.coroutineContext

   // exists to stop nesting
   @Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier") // function signature must match to stop nesting
   @Deprecated("Cannot nest leaf test inside another leaf test", level = DeprecationLevel.ERROR)
   suspend infix operator fun String.invoke(test: suspend TestScope.() -> Unit) {
   }
}
