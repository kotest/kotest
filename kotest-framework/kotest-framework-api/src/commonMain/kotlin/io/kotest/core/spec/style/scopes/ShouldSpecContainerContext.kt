package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 *
 */
@KotestDsl
class ShouldSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> should(name, test)
      }
   }

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName(name),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
   }

   @ExperimentalKotest
   fun context(name: String) = ContainerContextConfigBuilder(
      TestName(name),
      testContext,
      false
   ) { ShouldSpecContainerContext(it) }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName(name),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
   }

   @ExperimentalKotest
   fun xcontext(name: String) = ContainerContextConfigBuilder(
      TestName(name),
      testContext,
      true
   ) { ShouldSpecContainerContext(it) }

   suspend fun should(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         TestName("should ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = false,
      )
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         TestName("should ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = true,
      )
   }

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName("should ", name, true),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName("should ", name, true),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
}
