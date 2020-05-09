package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.createTestName

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * when("some test")
 * when("some test").config(...)
 * xwhen("some disabled test")
 * xwhen("some disabled test").config(...)
 *
 * and
 *
 * then("some test")
 * then("some test").config(...)
 * xthen("some disabled test").config(...)
 * xthen("some disabled test").config(...)
 *
 */
@Suppress("FunctionName")
@KotestDsl
class GivenScope(
   override val description: Description,
   override val context: ScopeContext
) : ContainerScope {

   suspend fun And(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, true)
   suspend fun and(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, true)

   private suspend fun addAnd(name: String, test: suspend GivenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("And: ", name)
      context.addContainerTest(testName, enabled) {
         GivenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.context.with(this)
         ).test()
      }
   }

   suspend fun When(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, true)
   suspend fun `when`(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, true)
   suspend fun xwhen(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, false)

   private suspend fun addWhen(name: String, test: suspend WhenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("When: ", name)
      context.addContainerTest(testName, enabled) {
         WhenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.context.with(this)
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
