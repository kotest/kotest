package io.kotest.core.spec.style.scopes

import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

@Deprecated("renamed to FeatureSpecContainerContext. Deprecated since 4.5.")
typealias FeatureScope = FeatureSpecContainerContext

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
class FeatureSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> feature(name, test)
         TestType.Test -> scenario(name, test)
      }
   }

   suspend fun feature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) {
      registerContainer(
         TestName("Feature: ", name, false),
         disabled = false,
         null
      ) { FeatureSpecContainerContext(this).test() }
   }

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) {
      registerContainer(
         TestName("Feature: ", name, true),
         disabled = true,
         null
      ) { FeatureSpecContainerContext(this).test() }
   }

   suspend fun scenario(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("Scenario: ", name, false), disabled = false, null, test)
   }

   suspend fun xscenario(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("Scenario: ", name, true), disabled = true, null, test)
   }

   suspend fun scenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName("Scenario: ", name, false),
         context = this,
         xdisabled = false,
      )
   }

   suspend fun xscenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName("Scenario: ", name, false),
         context = this,
         xdisabled = true,
      )
   }
}
