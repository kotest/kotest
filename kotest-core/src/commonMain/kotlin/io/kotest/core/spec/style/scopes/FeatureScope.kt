package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.*
import kotlin.coroutines.CoroutineContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * feature("some context")
 * xfeature("some disabled context")
 *
 * and
 *
 * scenario("some test")
 * scenario("some test").config(...)
 * xscenario("some test")
 * xscenario("some test").config(...)
 *
 */
@KotestDsl
class FeatureScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext
) : ContainerScope {

   suspend fun feature(name: String, test: suspend FeatureScope.() -> Unit) {
      val testName = TestName("Feature: ", name)
      addContainerTest(testName, xdisabled = false) {
         FeatureScope(
            this@FeatureScope.description.append(testName),
            this@FeatureScope.lifecycle,
            this,
            this@FeatureScope.defaultConfig,
            this@FeatureScope.coroutineContext
         ).test()
      }
   }

   suspend fun xfeature(name: String, test: suspend FeatureScope.() -> Unit) {
      val testName = TestName("Feature: ", name)
      addContainerTest(testName, xdisabled = true) {
         FeatureScope(
            this@FeatureScope.description.append(testName),
            this@FeatureScope.lifecycle,
            this,
            this@FeatureScope.defaultConfig,
            this@FeatureScope.coroutineContext
         ).test()
      }
   }

   suspend fun scenario(name: String, test: suspend TestContext.() -> Unit) {
      addContainerTest(TestName("Scenario: ", name), xdisabled = false, test = test)
   }

   suspend fun xscenario(name: String, test: suspend TestContext.() -> Unit) {
      addContainerTest(TestName("Scenario: ", name), xdisabled = true, test = test)
   }

   fun scenario(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(TestName("Scenario: ", name), testContext, defaultConfig, false)
   }

   fun xscenario(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(TestName("Scenario: ", name), testContext, defaultConfig, true)
   }
}
