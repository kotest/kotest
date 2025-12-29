package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * ```
 * feature("some context")
 * xfeature("some disabled context")
 * ```
 *
 * and
 *
 * ```
 * scenario("some test")
 * scenario("some test").config(...)
 * xscenario("some test")
 * xscenario("some test").config(...)
 * ```
 */
@KotestTestScope
class FeatureSpecContainerScope(
   val testScope: TestScope
) : AbstractContainerScope(testScope) {

   suspend fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      feature(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun ffeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      feature(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      feature(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun feature(name: String, xmethod: TestXMethod, test: suspend FeatureSpecContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = xmethod,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   suspend fun scenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun fscenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   suspend fun xscenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   suspend fun scenario(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Scenario: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun xfscenario(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Scenario: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.FOCUSED,
      )
   }

   suspend fun xscenario(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Scenario: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.DISABLED,
      )
   }

   private suspend fun scenario(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).withPrefix("Scenario: ").build(),
         xmethod = xmethod,
         config = null,
         test = test
      )
   }
}
