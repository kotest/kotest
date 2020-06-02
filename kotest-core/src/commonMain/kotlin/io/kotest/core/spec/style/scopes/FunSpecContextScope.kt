package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
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
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   /**
    * Adds a nested context scope to the scope.
    */
   suspend fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      addContainerTest(name, xdisabled = false) {
         FunSpecContextScope(
            this@FunSpecContextScope.description.append(name),
            this@FunSpecContextScope.lifecycle,
            this,
            this@FunSpecContextScope.defaultConfig
         ).test()
      }
   }

   fun test(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = false)
   fun xtest(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = true)

   suspend fun test(name: String, test: suspend TestContext.() -> Unit) = addTest(name, xdisabled = false, test = test)
   suspend fun xtest(name: String, test: suspend TestContext.() -> Unit) = addTest(name, xdisabled = true, test = test)
}
