package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

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
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend fun And(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun and(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun xand(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, xdisabled = true)
   suspend fun xAnd(name: String, test: suspend WhenScope.() -> Unit) = addAnd(name, test, xdisabled = true)

   private suspend fun addAnd(name: String, test: suspend WhenScope.() -> Unit, xdisabled: Boolean) {
      val testName = createTestName("And: ", name, true)
      addContainerTest(testName, xdisabled) {
         WhenScope(
            this@WhenScope.description.appendContainer(testName),
            this@WhenScope.lifecycle,
            this,
            this@WhenScope.defaultConfig,
            this@WhenScope.coroutineContext,
         ).test()
      }
   }

   fun then(name: String) =
      TestWithConfigBuilder(
         createTestName("Then: ", name, true),
         testContext,
         defaultConfig,
         xdisabled = false
      )

   fun Then(name: String) =
      TestWithConfigBuilder(
         createTestName("Then: ", name, true),
         testContext,
         defaultConfig,
         xdisabled = false
      )

   fun xthen(name: String) =
      TestWithConfigBuilder(createTestName("Then: ", name, true), testContext, defaultConfig, xdisabled = true)

   fun xThen(name: String) =
      TestWithConfigBuilder(createTestName("Then: ", name, true), testContext, defaultConfig, xdisabled = true)

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, xdisabled: Boolean) {
      addTest(createTestName("Then: ", name, true), xdisabled, test)
   }
}
