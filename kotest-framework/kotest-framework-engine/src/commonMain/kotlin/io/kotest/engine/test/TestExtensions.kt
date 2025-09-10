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
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.collect
import io.kotest.engine.config.ExtensionsOrder
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
      val extensions = testConfigResolver.extensions(testCase, ExtensionsOrder.GLOBAL_FIRST)
         .filterIsInstance<BeforeInvocationListener>()
      return extensions.map { listener ->
         runCatching {
            listener.beforeInvocation(testCase, invocation)
         }.mapError { ExtensionException.BeforeInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   suspend fun afterInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = testConfigResolver.extensions(testCase, ExtensionsOrder.LOCAL_FIRST)
         .filterIsInstance<AfterInvocationListener>()
      return extensions.map { listener ->
         runCatching {
            listener.afterInvocation(testCase, invocation)
         }.mapError { ExtensionException.AfterInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   /**
    * Invokes all beforeXYZ callbacks for this test, checking for the appropriate test type.
    * Returns a Result of [MultipleExceptions] if there are any exceptions.
    *
    * For before callbacks, the least specific (global) are applied first.
    */
   suspend fun beforeTestBeforeEachBeforeContainer(testCase: TestCase): Result<TestCase> {

      val extensions = testConfigResolver.extensions(testCase, ExtensionsOrder.GLOBAL_FIRST)
      val bt = extensions.filterIsInstance<BeforeTestListener>()
      val bc = extensions.filterIsInstance<BeforeContainerListener>()
      val be = extensions.filterIsInstance<BeforeEachListener>()

      val errors = bc.mapNotNull { listener ->
         runCatching {
            if (testCase.type == TestType.Container) listener.beforeContainer(testCase)
         }.mapError { ExtensionException.BeforeContainerException(it) }.exceptionOrNull()
      } + be.mapNotNull { listener ->
         runCatching {
            if (testCase.type == TestType.Test) listener.beforeEach(testCase)
         }.mapError { ExtensionException.BeforeEachException(it) }.exceptionOrNull()
      } + bt.mapNotNull { listener ->
         runCatching {
            listener.beforeAny(testCase)
            listener.beforeTest(testCase)
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
    *
    * For after callbacks, the most specific (test level) are applied first.
    */
   suspend fun afterTestAfterEachAfterContainer(testCase: TestCase, result: TestResult): Result<TestResult> {

      val extensions = testConfigResolver.extensions(testCase, ExtensionsOrder.LOCAL_FIRST)

      val at = extensions.filterIsInstance<AfterTestListener>()
      val ac = extensions.filterIsInstance<AfterContainerListener>()
      val ae = extensions.filterIsInstance<AfterEachListener>()

      val errors = at.mapNotNull { listener ->
         runCatching {
            listener.afterTest(testCase, result)
            listener.afterAny(testCase, result)
         }.mapError { ExtensionException.AfterAnyException(it) }.exceptionOrNull()
      } + ac.mapNotNull { listener ->
         runCatching {
            if (testCase.type == TestType.Container) listener.afterContainer(testCase, result)
         }.mapError { ExtensionException.AfterContainerException(it) }.exceptionOrNull()
      } + ae.mapNotNull { listener ->
         runCatching {
            if (testCase.type == TestType.Test) listener.afterEach(testCase, result)
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

      val execute = testConfigResolver.extensions(testCase, ExtensionsOrder.GLOBAL_FIRST)
         .filterIsInstance<TestCaseExtension>()
         .reversed()
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
      return testConfigResolver.extensions(testCase, ExtensionsOrder.LOCAL_FIRST)
         .filterIsInstance<LogExtension>()
   }

   /**
    * Executes all [IgnoredTestListener]s for this [TestCase].
    */
   suspend fun ignoredTestListenersInvocation(testCase: TestCase, reason: String?) {
      testConfigResolver.extensions(testCase, ExtensionsOrder.GLOBAL_FIRST)
         .filterIsInstance<IgnoredTestListener>()
         .forEach { it.ignoredTest(testCase, reason) }
   }
}
