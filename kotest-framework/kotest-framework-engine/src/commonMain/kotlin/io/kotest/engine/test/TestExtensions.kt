package io.kotest.engine.test

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterInvocationListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeInvocationListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.IgnoredTestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.collect
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.mapError
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.logging.LogExtension
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Used to invoke [Extension]s on tests.
 */
internal class TestExtensions(
   private val testConfigResolver: TestConfigResolver,
) {

   suspend fun beforeInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = testConfigResolver.extensions(testCase).filterIsInstance<BeforeInvocationListener>()
      return extensions.map {
         runCatching {
            it.beforeInvocation(testCase, invocation)
         }.mapError { ExtensionException.BeforeInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   suspend fun afterInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = testConfigResolver.extensions(testCase).filterIsInstance<AfterInvocationListener>()
      return extensions.map {
         runCatching {
            it.afterInvocation(testCase, invocation)
         }.mapError { ExtensionException.AfterInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   /**
    * Invokes all beforeXYZ callbacks for this test, checking for the appropriate test type.
    * Returns a Result of [MultipleExceptions] if there are any exceptions.
    */
   suspend fun beforeTestBeforeAnyBeforeContainer(testCase: TestCase): Result<TestCase> {

      val bt = testConfigResolver.extensions(testCase).filterIsInstance<BeforeTestListener>()
      val bc = testConfigResolver.extensions(testCase).filterIsInstance<BeforeContainerListener>()
      val be = testConfigResolver.extensions(testCase).filterIsInstance<BeforeEachListener>()

      val errors = bc.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Container) it.beforeContainer(testCase)
         }.mapError { ExtensionException.BeforeContainerException(it) }.exceptionOrNull()
      } + be.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Test) it.beforeEach(testCase)
         }.mapError { ExtensionException.BeforeEachException(it) }.exceptionOrNull()
      } + bt.mapNotNull {
         runCatching {
            it.beforeAny(testCase)
            it.beforeTest(testCase)
         }.mapError { ExtensionException.BeforeAnyException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(testCase)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   /**
    * Invokes all afterXYZ callbacks for this test.
    * Returns a Result of [MultipleExceptions] if there are any exceptions.
    */
   suspend fun afterTestAfterAnyAfterContainer(testCase: TestCase, result: TestResult): Result<TestResult> {

      val at = testConfigResolver.extensions(testCase).filterIsInstance<AfterTestListener>()
      val ac = testConfigResolver.extensions(testCase).filterIsInstance<AfterContainerListener>()
      val ae = testConfigResolver.extensions(testCase).filterIsInstance<AfterEachListener>()

      val errors = at.mapNotNull {
         runCatching {
            it.afterTest(testCase, result)
            it.afterAny(testCase, result)
         }.mapError { ExtensionException.AfterAnyException(it) }.exceptionOrNull()
      } + ac.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Container) it.afterContainer(testCase, result)
         }.mapError { ExtensionException.AfterContainerException(it) }.exceptionOrNull()
      } + ae.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Test) it.afterEach(testCase, result)
         }.mapError { ExtensionException.AfterEachException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(result)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   /**
    * Executes the [TestCaseExtension]s for this [TestCase].
    */
   suspend fun intercept(
      testCase: TestCase,
      context: TestScope,
      inner: NextTestExecutionInterceptor,
   ): TestResult {

      val execute = testConfigResolver.extensions(testCase).filterIsInstance<TestCaseExtension>()
         .fold(inner) { execute, ext ->
            NextTestExecutionInterceptor { tc, ctx ->
               ext.intercept(tc) {
                  // the user's intercept method is free to change the context of the coroutine
                  // to support this, we should switch the context used by the test case context
                  execute(it, ctx.withCoroutineContext(coroutineContext))
               }
            }
         }

      return execute(testCase, context)
   }

   /**
    * Returns the [LogExtension]s applicable to this [TestCase].
    */
   fun logExtensions(testCase: TestCase): List<LogExtension> {
      return testConfigResolver.extensions(testCase).filterIsInstance<LogExtension>()
   }

   /**
    * Executes all [IgnoredTestListener]s for this [TestCase].
    */
   suspend fun ignoredTestListenersInvocation(testCase: TestCase, reason: String?) {
      testConfigResolver.extensions(testCase).filterIsInstance<IgnoredTestListener>()
         .forEach { it.ignoredTest(testCase, reason) }
   }
}
