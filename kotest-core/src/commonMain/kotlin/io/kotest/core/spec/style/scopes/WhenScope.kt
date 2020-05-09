package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

/**
 * A context that allows tests to be registered using the syntax:
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
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   suspend fun And(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, true)
   suspend fun and(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, true)

   private suspend fun addAnd(name: String, test: suspend WhenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("And: ", name)
      addContainerTest(testName, enabled) {
         WhenScope(
            this@WhenScope.description.append(testName),
            this@WhenScope.lifecycle,
            this,
            this@WhenScope.defaultConfig
         ).test()
      }
   }

   fun then(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = false)
   fun Then(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = false)

   fun xthen(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = true)
   fun xThen(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = true)

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, true)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, true)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, false)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, enabled: Boolean) {
      addTest(createTestName("Then: ", name), enabled, test)
   }
}
