package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.plan.createTestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import kotlin.coroutines.CoroutineContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some context")
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestDsl
class FunSpecContainerContext(
   private val testContext: TestContext,
) : ContainerContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override val executionContext: ExecutionContext = testContext.executionContext
   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> test(name, test)
      }
   }

   /**
    * Adds a container test to this context.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { FunSpecContainerContext(this).test() }
         )
      )
   }

   /**
    * Adds a container test to this context.
    */
   @ExperimentalKotest
   fun context(name: String) = ContainerContextConfigBuilder(
      name = createTestName(name),
      context = testContext,
      xdisabled = false,
      contextFn = { FunSpecContainerContext(it) }
   )

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { FunSpecContainerContext(this).test() }
         )
      )
   }

   @ExperimentalKotest
   fun xcontext(name: String) = ContainerContextConfigBuilder(
      createTestName(name),
      testContext,
      true
   ) { FunSpecContainerContext(it) }

   /**
    * Adds a test case to this context, expecting config.
    */
   suspend fun test(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.testPath().append(name))
      return TestWithConfigBuilder(
         createTestName(name),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = false
      )
   }

   /**
    * Adds a disabled test case to this context, expecting config.
    */
   suspend fun xtest(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.testPath().append(name))
      return TestWithConfigBuilder(
         createTestName(name),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = true
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = test,
         )
      )

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = test,
         )
      )
}
