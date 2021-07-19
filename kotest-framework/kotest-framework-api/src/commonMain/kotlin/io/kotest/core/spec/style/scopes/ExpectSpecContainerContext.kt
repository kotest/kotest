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
 * context("some test")
 * xcontext("some disabled test")
 *
 * and
 *
 * expect("some test")
 * expect("some test").config(...)
 * xexpect("some test")
 * xexpect("some test").config(...)
 *
 */
@KotestDsl
class ExpectSpecContainerContext(
   val testContext: TestContext,
) : ContainerContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override val executionContext: ExecutionContext = testContext.executionContext
   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> expect(name, test)
      }

   }

   suspend fun context(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "Context: ", null, false),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId
         ) { ExpectSpecContainerContext(this).test() }
      )
   }

   suspend fun xcontext(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "Context: ", null, false),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId
         ) { ExpectSpecContainerContext(this).test() }
      )
   }

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "Expect: ", null, false),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = test
         )
      )
   }

   suspend fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name, "Expect: ", null, false),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = test
         )
      )
   }

   suspend fun expect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.testPath().append(name))
      return TestWithConfigBuilder(
         createTestName(name, "Expect: ", null, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = false,
      )
   }

   suspend fun xexpect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.testPath().append(name))
      return TestWithConfigBuilder(
         createTestName(name, "Expect: ", null, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = true,
      )
   }

}
