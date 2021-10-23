package io.kotest.engine.test

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
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.withCoroutineContext
import io.kotest.fp.collect
import io.kotest.fp.mapError
import kotlin.coroutines.coroutineContext

/**
 * Used to invoke extension points on tests.
 */
internal class TestExtensions(private val registry: ExtensionRegistry) {

   /**
    * Returns all [Extension]s applicable to a [TestCase]. This includes extensions
    * included in test case config, those at the spec level, and project wide from
    * the registry.
    */
   fun extensions(testCase: TestCase): List<Extension> {
      return testCase.config.listeners + //
         testCase.config.extensions + //
         testCase.spec.extensions() + // overriding the extensions function in the spec
         testCase.spec.listeners() + // overriding the listeners function in the spec
         testCase.spec.functionOverrideCallbacks() + // spec level dsl eg beforeTest { }
         testCase.spec.registeredExtensions() + // added to the spec via register
         registry.all() // globals
   }

   suspend fun beforeInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = extensions(testCase).filterIsInstance<BeforeInvocationListener>()
      return extensions.map {
         runCatching {
            it.beforeInvocation(testCase, invocation)
         }.mapError { BeforeInvocationException(it) }
      }.collect { if (it.size == 1) it.first() else MultipleExceptions(it) }.map { testCase }
   }

   suspend fun afterInvocation(testCase: TestCase, invocation: Int): Result<TestCase> {
      val extensions = extensions(testCase).filterIsInstance<AfterInvocationListener>()
      return extensions.map {
         runCatching {
            it.afterInvocation(testCase, invocation)
         }.mapError { AfterInvocationException(it) }
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
         }.mapError { BeforeContainerException(it) }.exceptionOrNull()
      } + be.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Test) it.beforeEach(testCase)
         }.mapError { BeforeEachException(it) }.exceptionOrNull()
      } + bt.mapNotNull {
         runCatching {
            it.beforeAny(testCase)
         }.mapError { BeforeAnyException(it) }.exceptionOrNull()
      } + bt.mapNotNull {
         runCatching {
            it.beforeTest(testCase)
         }.mapError { BeforeTestException(it) }.exceptionOrNull()
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
   suspend fun afterTestAfterAnyAfterContainer(testCase: TestCase, result: TestResult): Result<TestCase> {

      val at = extensions(testCase).filterIsInstance<AfterTestListener>()
      val ac = extensions(testCase).filterIsInstance<AfterContainerListener>()
      val ae = extensions(testCase).filterIsInstance<AfterEachListener>()

      val errors = at.mapNotNull {
         runCatching {
            it.afterTest(testCase, result)
         }.mapError { AfterTestException(it) }.exceptionOrNull()
      } + at.mapNotNull {
         runCatching {
            it.afterAny(testCase, result)
         }.mapError { AfterAnyException(it) }.exceptionOrNull()
      } + ac.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Container) it.afterContainer(testCase, result)
         }.mapError { AfterContainerException(it) }.exceptionOrNull()
      } + ae.mapNotNull {
         runCatching {
            if (testCase.type == TestType.Test) it.afterEach(testCase, result)
         }.mapError { AfterEachException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(testCase)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   /**
    * Executes the [TestCaseExtension]s for this [TestCase].
    */
   suspend fun intercept(
      testCase: TestCase,
      context: TestContext,
      inner: suspend (TestCase, TestContext) -> TestResult,
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
}

class MultipleExceptions(val causes: List<Throwable>) : RuntimeException(causes.first())
class BeforeInvocationException(cause: Throwable) : RuntimeException(cause)
class AfterInvocationException(cause: Throwable) : RuntimeException(cause)
class BeforeTestException(cause: Throwable) : RuntimeException(cause)
class AfterTestException(cause: Throwable) : RuntimeException(cause)
class BeforeEachException(cause: Throwable) : RuntimeException(cause)
class AfterEachException(cause: Throwable) : RuntimeException(cause)
class BeforeContainerException(cause: Throwable) : RuntimeException(cause)
class AfterContainerException(cause: Throwable) : RuntimeException(cause)
class BeforeAnyException(cause: Throwable) : RuntimeException(cause)
class AfterAnyException(cause: Throwable) : RuntimeException(cause)
