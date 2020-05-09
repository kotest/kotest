package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

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
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   suspend fun And(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, true)
   suspend fun and(name: String, test: suspend GivenScope.() -> Unit) = addAnd(name, test, true)

   private suspend fun addAnd(name: String, test: suspend GivenScope.() -> Unit, xdisabled: Boolean) {
      val testName = createTestName("And: ", name)
      addContainerTest(testName, xdisabled) {
         GivenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.lifecycle,
            this,
            this@GivenScope.defaultConfig
         ).test()
      }
   }

   suspend fun When(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, true)
   suspend fun `when`(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, true)
   suspend fun xwhen(name: String, test: suspend WhenScope.() -> Unit) = addWhen(name, test, false)

   private suspend fun addWhen(name: String, test: suspend WhenScope.() -> Unit, enabled: Boolean) {
      val testName = createTestName("When: ", name)
      addContainerTest(testName, enabled) {
         WhenScope(
            this@GivenScope.description.append(testName),
            this@GivenScope.lifecycle,
            this,
            this@GivenScope.defaultConfig
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
