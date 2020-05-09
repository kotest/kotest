package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 *
 */
@KotestDsl
class ShouldSpecContextScope(
   override val description: Description,
   override val context: ScopeContext
) : ContainerScope {

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      context.addContainerTest(name) {
         ShouldSpecContextScope(
            this@ShouldSpecContextScope.description.append(name),
            this@ShouldSpecContextScope.context.with(this)
         ).test()
      }
   }

   fun should(name: String) = TestWithConfigBuilder(createTestName("should ", name), context)

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      context.addTest(createTestName("should ", name), test)
}
