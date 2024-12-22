package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
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
      feature(name = name, disabled = false, test = test)
   }

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      feature(name = name, disabled = true, test = test)
   }

   private suspend fun feature(name: String, disabled: Boolean, test: suspend FeatureSpecContainerScope.() -> Unit) {
      registerContainer(
         TestName("Feature: ", name, false),
         disabled = disabled,
         null
      ) { FeatureSpecContainerScope(this).test() }
   }

   suspend fun scenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xdisabled = false, test = test)
   }

   suspend fun xscenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xdisabled = true, test = test)
   }

   suspend fun scenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName("Scenario: ", name, false),
         context = this,
         xdisabled = false,
      )
   }

   suspend fun xscenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName("Scenario: ", name, false),
         context = this,
         xdisabled = true,
      )
   }

   private suspend fun scenario(name: String, xdisabled: Boolean, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestName("Scenario: ", name, false), disabled = xdisabled, config = null, test = test)
   }
}
