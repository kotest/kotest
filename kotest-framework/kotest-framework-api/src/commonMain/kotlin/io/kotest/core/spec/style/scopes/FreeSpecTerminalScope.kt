package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

@KotestTestScope
class FreeSpecTerminalScope(val parentTestScope: TestScope) : TerminalScope() {

   override val testCase: TestCase = parentTestScope.testCase
   override val coroutineContext: CoroutineContext = parentTestScope.coroutineContext

   // exists to stop nesting
   @Deprecated("Cannot nest leaf test inside another leaf test", level = DeprecationLevel.ERROR)
   suspend infix operator fun String.invoke(test: suspend TestScope.() -> Unit) {
   }

   @ExperimentalCoroutinesApi
   override val testScope: kotlinx.coroutines.test.TestScope
      get() = parentTestScope.testScope
}
