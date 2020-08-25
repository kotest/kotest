package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

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
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   /**
    * Adds a nested context scope to the scope.
    */
   suspend fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      addContainerTest(testName, xdisabled = false) {
         FunSpecContextScope(
            this@FunSpecContextScope.description.appendContainer(testName),
            this@FunSpecContextScope.lifecycle,
            this,
            this@FunSpecContextScope.defaultConfig,
            this@FunSpecContextScope.coroutineContext,
         ).test()
      }
   }

   fun test(name: String) =
      TestWithConfigBuilder(createTestName(name), testContext, defaultConfig, xdisabled = false)

   fun xtest(name: String) =
      TestWithConfigBuilder(createTestName(name), testContext, defaultConfig, xdisabled = true)

   suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = false, test = test)

   suspend fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = true, test = test)
}
