package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.test.NestedTest
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
class FeatureSpecContainerScope(
   val testScope: TestScope
) : AbstractContainerScope<FeatureSpecContainerScope>(testScope) {

   override suspend fun registerTestCase(nested: NestedTest) = testScope.registerTestCase(nested)

   suspend fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addFeature(name, false, test)
   }

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addFeature(name, true, test)
   }

   private suspend fun addFeature(name: String, disabled: Boolean, test: suspend FeatureSpecContainerScope.() -> Unit) {
      registerContainer(
         TestName("Feature: ", name, false),
         disabled = disabled,
         null
      ) { FeatureSpecContainerScope(this).test() }
   }

   suspend fun scenario(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName("Scenario: ", name, false), disabled = false, null, test)
   }

   suspend fun xscenario(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName("Scenario: ", name, true), disabled = true, null, test)
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

   override suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend FeatureSpecContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addFeature(nameFn(t), false) { this.test(t) }
      }
   }
}
