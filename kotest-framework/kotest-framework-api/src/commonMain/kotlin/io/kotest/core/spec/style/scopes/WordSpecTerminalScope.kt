package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

/**
 * This scope exists so we can add a deprecated `should` method to stop nesting a `should` inside a `should`
 */
@KotestTestScope
class WordSpecTerminalScope(val parentTestScope: TestScope) : TerminalScope() {

   override val coroutineContext: CoroutineContext = parentTestScope.coroutineContext
   override val testCase: TestCase = parentTestScope.testCase

   // we need to override the should method to stop people nesting a should inside a should
   @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
   infix fun String.should(init: () -> Unit) = { init() }

   @ExperimentalCoroutinesApi
   override val testScope: kotlinx.coroutines.test.TestScope
      get() = parentTestScope.testScope
}
