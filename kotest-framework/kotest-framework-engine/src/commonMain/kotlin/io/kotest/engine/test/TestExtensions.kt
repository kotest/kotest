package io.kotest.engine.test

import io.kotest.engine.collect
import io.kotest.engine.mapError
import io.kotest.core.config.ExtensionRegistry
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
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.test.logging.LogExtension
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Used to invoke [Extension]s on tests.
 */
internal class TestExtensions(private val registry: ExtensionRegistry) {

   /**
    * Returns all [Extension]s applicable to a [TestCase]. This includes extensions
    * included in test case config, those at the spec level, and project wide from
    * the registry.
    */
   fun extensions(testCase: TestCase): List<Extension> {
      return registry.all() + // globals
         testCase.spec.extensions() + // overriding the extensions function in the spec
         testCase.spec.functionOverrideCallbacks() + // spec level dsl eg beforeTest { }
         testCase.spec.registeredExtensions() + // added to the spec via register
         testCase.config.extensions // extensions coming from the test config block itself
   }

   suspend fun beforeInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = extensions(testCase).filterIsInstance<BeforeInvocationListener>()
      return extensions.map {
         runCatching {
            it.beforeInvocation(testCase, invocation)
         }.mapError { ExtensionException.BeforeInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   suspend fun afterInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = extensions(testCase).filterIsInstance<AfterInvocationListener>()
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

      val bt = extensions(testCase).filterIsInstance<BeforeTestListener>()
      val bc = extensions(testCase).filterIsInstance<BeforeContainerListener>()
      val be = extensions(testCase).filterIsInstance<BeforeEachListener>()

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
    * Invokes all beforeXYZ callbacks for this test.
    * Returns a Result of [MultipleExceptions] if there are any exceptions.
    */
   suspend fun afterTestAfterAnyAfterContainer(testCase: TestCase, result: TestResult): Result<TestResult> {

      val at = extensions(testCase).filterIsInstance<AfterTestListener>()
      val ac = extensions(testCase).filterIsInstance<AfterContainerListener>()
      val ae = extensions(testCase).filterIsInstance<AfterEachListener>()

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
      inner: suspend (TestCase, TestScope) -> TestResult,
   ): TestResult {

      val execute = extensions(testCase).filterIsInstance<TestCaseExtension>()
         .foldRight(inner) { extension, execute ->
            { tc, ctx ->
               extension.intercept(tc) {
                  // the user's intercept method is free to change the context of the coroutine
                  // to support this, we should switch the context used by the test case context
                  execute(it, ctx.withCoroutineContext(coroutineContext))
               }
            }
         }

      return execute(testCase, context)
   }

   fun logExtensions(testCase: TestCase): List<LogExtension> {
      return extensions(testCase).filterIsInstance<LogExtension>()
   }
}
