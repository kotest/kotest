package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
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
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      addContainerTest(name, xdisabled = false) {
         ShouldSpecContextScope(
            this@ShouldSpecContextScope.description.append(name),
            this@ShouldSpecContextScope.lifecycle,
            this,
            this@ShouldSpecContextScope.defaultConfig
         ).test()
      }
   }

   fun should(name: String) = TestWithConfigBuilder(createTestName("should ", name), testContext, defaultConfig, false)
   fun xshould(name: String) = TestWithConfigBuilder(createTestName("should ", name), testContext, defaultConfig, true)

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name), xdisabled = false, test = test)

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name), xdisabled = true, test = test)
}
