package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.createTestName

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * then("some test")
 * then("some test").config(...)
 *
 * or disabled tests via:
 *
 * xthen("some disabled test")
 * xthen("some disabled test").config(...)
 *
 */
@Suppress("FunctionName")
@KotestDsl
class WhenScope(
   override val description: Description,
   override val context: ScopeContext
) : ContainerScope {

   suspend fun And(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, true)
   suspend fun and(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, true)

   private suspend fun addAnd(name: String, test: suspend WhenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("And: ", name)
      context.addContainerTest(testName, enabled) {
         WhenScope(
            this@WhenScope.description.append(testName),
            this@WhenScope.context.with(this)
         ).test()
      }
   }

   fun then(name: String) = TestWithConfigBuilder(name, context)
   fun Then(name: String) = TestWithConfigBuilder(name, context)

   suspend fun Then(name: String, test: suspend TerminalScope.() -> Unit) = addThen(name, test, true)
   suspend fun then(name: String, test: suspend TerminalScope.() -> Unit) = addThen(name, test, true)
   suspend fun xthen(name: String, test: suspend TerminalScope.() -> Unit) = addThen(name, test, false)

   private suspend fun addThen(name: String, test: suspend TerminalScope.() -> Unit, enabled: Boolean) {
      context.addContainerTest(createTestName("Then: ", name), enabled) { TerminalScope(this).test() }
   }
}
