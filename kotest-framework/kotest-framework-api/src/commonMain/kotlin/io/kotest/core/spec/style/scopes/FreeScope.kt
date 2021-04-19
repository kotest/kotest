package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

class FreeSpecTerminalContext(
   val testContext: TestContext,
) : TestContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) = error("Cannot nest a test inside a terminal scope")
}

class FreeScope(
   val testContext: TestContext,
) : ContainerContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> name.minus(test)
         TestType.Test -> name.invoke(test)
      }
   }

   /**
    * Creates a new container scope inside this spec.
    */
   suspend infix operator fun String.minus(test: suspend FreeScope.() -> Unit) {
      registerTestCase(createNestedTest(this, TestType.Container) { FreeScope(this).test() })
   }

   /**
    * Creates a new terminal test scope inside this spec.
    */
   suspend infix operator fun String.invoke(test: suspend FreeSpecTerminalContext.() -> Unit) {
      registerTestCase(createNestedTest(this, TestType.Test) { FreeSpecTerminalContext(this).test() })
   }

   private fun createNestedTest(name: String, type: TestType, test: suspend TestContext.() -> Unit): NestedTest {
      return createNestedTest(
         name = this@FreeScope.testCase.description.append(createTestName(name), type).name,
         xdisabled = false,
         config = testCase.spec.resolvedDefaultConfig(),
         type = type,
         descriptor = null,
         factoryId = null,
         test = test
      )
   }

   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      test: suspend TestContext.() -> Unit,
   ) = TestWithConfigBuilder(
      createTestName(this),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = false,
   ).config(
      enabled,
      invocations,
      threads,
      tags,
      timeout,
      extensions,
      enabledIf,
      invocationTimeout,
      severity,
      test
   )
}
