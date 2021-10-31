package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import kotlin.coroutines.CoroutineContext

/**
 * This scope exists so we can add a deprecated `should` method to stop nesting a `should` inside a `should`
 */
@KotestDsl
class WordSpecTerminalScope(val testScope: TestScope) : TestScope {

   override suspend fun registerTestCase(nested: NestedTest) {
      testScope.registerTestCase(nested)
   }

   override val coroutineContext: CoroutineContext = testScope.coroutineContext
   override val testCase: TestCase = testScope.testCase

   // we need to override the should method to stop people nesting a should inside a should
   @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
   infix fun String.should(init: () -> Unit) = { init() }
}
