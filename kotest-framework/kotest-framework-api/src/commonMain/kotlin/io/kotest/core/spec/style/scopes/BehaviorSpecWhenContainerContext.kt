package io.kotest.core.spec.style.scopes

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
 * then("some test")
 * then("some test").config(...)
 *
 * or disabled tests via:
 *
 * xthen("some disabled test")
 * xthen("some disabled test").config(...)
 *
 */
@Suppress("FunctionName")
@KotestDsl
class BehaviorSpecWhenContainerContext(
   val testContext: TestContext,
) : ContainerContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override val executionContext: ExecutionContext = testContext.executionContext
   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> and(name, test)
         TestType.Test -> then(name, test)
      }
   }

   suspend fun And(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun and(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun xand(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = true)
   suspend fun xAnd(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = true)

   private suspend fun addAnd(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit, xdisabled: Boolean) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "And: ", null, true),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { BehaviorSpecWhenContainerContext(this).test() }
         )
      )
   }

   fun then(name: String) = TestWithConfigBuilder(
      createTestName(name, "Then: ", null, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = false
   )

   fun Then(name: String) = TestWithConfigBuilder(
      createTestName(name, "Then: ", null, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = false
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      createTestName(name, "Then: ", null, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = true
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      createTestName(name, "Then: ", null, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = true
   )

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, xdisabled: Boolean) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "Then: ", null, true),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = { BehaviorSpecWhenContainerContext(this).test() }
         )
      )
   }
}
