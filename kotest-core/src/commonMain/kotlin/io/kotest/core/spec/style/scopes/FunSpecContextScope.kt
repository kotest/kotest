package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestDsl
class FunSpecContextScope(
   override val description: Description,
   override val context: ScopeContext
) : ContainerScope {

   /**
    * Adds a nested context scope to the scope.
    */
   suspend fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      context.addContainerTest(name) {
         FunSpecContextScope(
            this@FunSpecContextScope.description.append(name),
            this@FunSpecContextScope.context.with(this)
         ).test()
      }
   }

   fun test(name: String) = TestWithConfigBuilder(name, context)

   suspend fun test(name: String, test: suspend TestContext.() -> Unit) = context.addTest(name, test)
}
