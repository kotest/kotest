package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.*
import kotlin.coroutines.CoroutineContext

/**
 * A context that allows tests to be registered using the syntax:
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
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext
) : ContainerScope {

   suspend fun And(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun and(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun xand(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, xdisabled = true)

   private suspend fun addAnd(name: String, test: suspend GivenScope.() -> Unit, xdisabled: Boolean) {
      val testName = TestName("And", name)
      addContainerTest(testName, xdisabled) {
         GivenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.lifecycle,
            this,
            this@GivenScope.defaultConfig,
            this@GivenScope.coroutineContext
         ).test()
      }
   }

   suspend fun When(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, xdisabled = false)
   suspend fun `when`(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, xdisabled = false)
   suspend fun xwhen(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, xdisabled = true)

   private suspend fun addWhen(name: String, test: suspend WhenScope.() -> Unit, xdisabled: Boolean) {
      val testName = TestName("When", name)
      addContainerTest(testName, xdisabled) {
         WhenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.lifecycle,
            this,
            this@GivenScope.defaultConfig,
            this@GivenScope.coroutineContext
         ).test()
      }
   }

   fun then(name: String) = TestWithConfigBuilder(TestName(name), testContext, defaultConfig, xdisabled = false)
   fun Then(name: String) = TestWithConfigBuilder(TestName(name), testContext, defaultConfig, xdisabled = false)
   fun xthen(name: String) = TestWithConfigBuilder(TestName(name), testContext, defaultConfig, xdisabled = true)

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, xdisabled: Boolean) {
      addTest(TestName("Then", name), xdisabled, test)
   }
}
