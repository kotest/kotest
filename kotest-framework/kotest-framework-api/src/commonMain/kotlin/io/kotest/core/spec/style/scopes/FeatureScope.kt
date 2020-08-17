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
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend fun feature(name: String, test: suspend FeatureScope.() -> Unit) {
      val testName = createTestName("Feature: ", name, false)
      addContainerTest(testName, xdisabled = false) {
         FeatureScope(
            this@FeatureScope.description.appendContainer(testName),
            this@FeatureScope.lifecycle,
            this,
            this@FeatureScope.defaultConfig,
            this@FeatureScope.coroutineContext,
         ).test()
      }
   }

   suspend fun xfeature(name: String, test: suspend FeatureScope.() -> Unit) {
      val testName = createTestName("Feature: ", name, false)
      addContainerTest(testName, xdisabled = true) {
         FeatureScope(
            this@FeatureScope.description.appendContainer(testName),
            this@FeatureScope.lifecycle,
            this,
            this@FeatureScope.defaultConfig,
            this@FeatureScope.coroutineContext,
         ).test()
      }
   }

   suspend fun scenario(name: String, test: suspend TestContext.() -> Unit) {
      addContainerTest(createTestName("Scenario: ", name, false), xdisabled = false, test = test)
   }

   suspend fun xscenario(name: String, test: suspend TestContext.() -> Unit) {
      addContainerTest(createTestName("Scenario: ", name, false), xdisabled = true, test = test)
   }

   fun scenario(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(
         createTestName("Scenario: ", name, false),
         testContext,
         defaultConfig,
         false,
      )
   }

   fun xscenario(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(
         createTestName("Scenario: ", name, false),
         testContext,
         defaultConfig,
         true,
      )
   }
}
