package io.kotest.engine.events

import io.kotest.core.config.configuration
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.resolvedTestListeners
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.fp.Try

/**
 * Invokes all before test callbacks for this test, taking the listeners from
 * those present at the spec level and the project level.
 */
suspend fun TestCase.invokeAllBeforeTestCallbacks(): Try<TestCase> =
   Try {
      spec.resolvedTestListeners() + configuration.extensions().filterIsInstance<TestListener>()
   }.fold({
      Try.Failure(it)
   }, { listeners ->
      listeners.map {
         Try {
            if (type == TestType.Container) it.beforeContainer(this)
            if (type == TestType.Test) it.beforeEach(this)
            it.beforeAny(this)
            it.beforeTest(this)
            this
         }
      }.find { it.isFailure() } ?: Try { this }
   })

/**
 * Invokes all after test callbacks for this test, taking the listeners from
 * those present at the config level, spec level and the project level.
 */
suspend fun TestCase.invokeAllAfterTestCallbacks(result: TestResult): Try<TestCase> =
   Try {
      this.config.listeners + spec.resolvedTestListeners() + configuration.extensions().filterIsInstance<TestListener>()
   }.fold({
      Try.Failure(it)
   }, { listeners ->
      Try {
         var currentResult = result
         var currentException: Error? = null
         listeners.forEach {
            try {
               it.afterTest(this, currentResult)
               it.afterAny(this, currentResult)
               if (type == TestType.Test) it.afterEach(this, currentResult)
               if (type == TestType.Container) it.afterContainer(this, currentResult)
            } catch (e: Error) {
               if (!listOf(TestStatus.Failure, TestStatus.Error).contains(currentResult.status)) {
                  currentResult = TestResult(
                     status = TestStatus.Failure,
                     error = e,
                     reason = "AfterTest Failed: ${e.message}",
                     duration = currentResult.duration
                  )
                  currentException = e
               }
            }
         }
         currentException?.let { throw Error(it) }

         this
      }
   })
