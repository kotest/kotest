package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

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

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      feature(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun feature(name: String, xmethod: TestXMethod, test: suspend FeatureSpecContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder
            .builder(featureName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { FeatureSpecContainerScope(this).test() }
      )
   }

   fun feature(name: String) =
      addFeature(name = name, xmethod = TestXMethod.NONE)

   fun xfeature(name: String) =
      addFeature(name = name, xmethod = TestXMethod.DISABLED)

   private fun addFeature(name: String, xmethod: TestXMethod) =
      ContainerWithConfigBuilder(
         name = featureName(name),
         context = this,
         xmethod = xmethod,
      ) { FeatureSpecContainerScope(it) }

   suspend fun scenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun xscenario(name: String, test: suspend TestScope.() -> Unit) {
      scenario(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   suspend fun scenario(name: String): TestWithConfigBuilder {
      val testName = scenarioName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun fscenario(name: String): TestWithConfigBuilder {
      val testName = scenarioName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.FOCUSED,
      )
   }


   suspend fun xscenario(name: String): TestWithConfigBuilder {
      val testName = scenarioName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.DISABLED,
      )
   }

   private suspend fun scenario(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder
            .builder(scenarioName(name), TestType.Test)
            .withXmethod(xmethod)
            .build { WordSpecWhenContainerScope(this).test() }
      )
   }

   private fun featureName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Feature: ").build()
   private fun scenarioName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Scenario: ").build()
}
