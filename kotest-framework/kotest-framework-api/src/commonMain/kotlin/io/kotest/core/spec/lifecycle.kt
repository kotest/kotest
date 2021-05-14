package io.kotest.core.spec

import io.kotest.core.config.configuration
import io.kotest.core.config.testListeners
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterProjectListenerException
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.BeforeProjectListenerException
import io.kotest.core.listeners.Listener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import io.kotest.core.test.TestType
import io.kotest.core.test.TestStatus

fun <T : Listener> List<T>.resolveName(): List<Pair<String, T>> =
   groupBy { it.name }
      .flatMap { entry ->
         if (entry.value.size > 1) {
            entry.value.mapIndexed { index, listener -> "${listener.name}_$index" to listener }
         } else {
            entry.value.map { it.name to it }
         }
      }

/**
 * Invokes any afterProject functions from the given listeners.
 */
suspend fun List<Listener>.afterProject(): Try<List<AfterProjectListenerException>> = Try {
   log { "invokeAfterProject" }
   filterIsInstance<AfterProjectListener>()
      .resolveName()
      .map { it.first to Try { it.second.afterProject() } }
      .filter { it.second.isFailure() }
      .map {
         AfterProjectListenerException(
            it.first,
            (it.second as Try.Failure).error
         )
      }
}.mapFailure { AfterProjectListenerException("afterProjectsInvocation", it) }

/**
 * Invokes the beforeProject listeners.
 */
suspend fun List<Listener>.beforeProject(): Try<List<BeforeProjectListenerException>> = Try {
   log { "invokeBeforeProject" }
   filterIsInstance<BeforeProjectListener>()
      .resolveName()
      .map { it.first to Try { it.second.beforeProject() } }
      .filter { it.second.isFailure() }
      .map {
         BeforeProjectListenerException(
            it.first,
            (it.second as Try.Failure).error
         )
      }
}.mapFailure { BeforeProjectListenerException("beforeProjectsInvocation", it) }

/**
 * Invokes all before test callbacks for this test, taking the listeners from
 * those present at the spec level and the project level.
 */
suspend fun TestCase.invokeAllBeforeTestCallbacks(): Try<TestCase> =
   Try {
      spec.resolvedTestListeners() + configuration.testListeners()
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
      this.config.listeners + spec.resolvedTestListeners() + configuration.testListeners()
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

suspend fun TestCase.invokeBeforeInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.beforeInvocation(this, k)
   }
}

suspend fun TestCase.invokeAfterInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.afterInvocation(this, k)
   }
}

/**
 * Notifies the user listeners that a [Spec] is starting.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeBeforeSpec(): Try<Spec> = Try {
   log { "invokeBeforeSpec $this" }
   val listeners = resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.beforeSpec(this)
   }
   this
}

/**
 * Notifies the user listeners that a [Spec] has finished.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeAfterSpec(): Try<Spec> = Try {
   log { "invokeAfterSpec $this" }

   registeredAutoCloseables().let { closeables ->
      log { "Closing ${closeables.size} autocloseables [$closeables]" }
      closeables.forEach { it.value.close() }
   }

   val listeners = resolvedTestListeners() + configuration.testListeners()
   listeners.forEach { it.afterSpec(this) }
   this
}
