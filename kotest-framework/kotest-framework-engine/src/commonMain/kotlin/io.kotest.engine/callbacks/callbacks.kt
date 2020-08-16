package io.kotest.engine.callbacks

import io.kotest.core.config.configuration
import io.kotest.core.config.testCaseExtensions
import io.kotest.core.config.testListeners
import io.kotest.core.config.Project
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.resolvedExtensions
import io.kotest.engine.spec.resolvedTestListeners
import io.kotest.fp.Try
import io.kotest.mpp.log

fun List<ProjectListener>.resolveName() =
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
   log("invokeAfterProject")
   filterIsInstance<ProjectListener>()
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
 * Invokes the beforeProject listeners, and prints project config using [dumpProjectConfig].
 */
suspend fun List<Listener>.beforeProject(): Try<List<BeforeProjectListenerException>> = Try {
   log("invokeBeforeProject")
   Project.dumpProjectConfig()

   filterIsInstance<ProjectListener>()
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
      spec.resolvedTestListeners() + Project.testListeners()
   }.fold({
      Try.Failure(it)
   }, { listeners ->
      Try {
         listeners.forEach {
            if (type == io.kotest.core.test.TestType.Container) it.beforeContainer(this)
            if (type == io.kotest.core.test.TestType.Test) it.beforeEach(this)
            it.beforeAny(this)
            it.beforeTest(this)
         }

         this
      }
   })

/**
 * Invokes all after test callbacks for this test, taking the listeners from
 * those present at the config level, spec level and the project level.
 */
suspend fun TestCase.invokeAllAfterTestCallbacks(result: TestResult): Try<TestCase> =
   Try {
      this.config.listeners + spec.resolvedTestListeners() + Project.testListeners()
   }.fold({
      Try.Failure(it)
   }, { listeners ->
      Try {
         listeners.forEach {
            it.afterTest(this, result)
            it.afterAny(this, result)
            if (type == io.kotest.core.test.TestType.Test) it.afterEach(this, result)
            if (type == io.kotest.core.test.TestType.Container) it.afterContainer(this, result)
         }

         this
      }
   })

suspend fun TestCase.invokeBeforeInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.beforeInvocation(this, k)
   }
}

suspend fun TestCase.invokeAfterInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.afterInvocation(this, k)
   }
}

/**
 * Notifies the user listeners that a [AbstractSpec] is starting.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeBeforeSpec(): Try<Spec> = Try {
   log("invokeBeforeSpec $this")
   val listeners = resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.beforeSpec(this)
   }
   this
}

/**
 * Notifies the user listeners that a [AbstractSpec] has finished.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeAfterSpec(): Try<Spec> = Try {
   log("invokeAfterSpec $this")

   registeredAutoCloseables().let { closeables ->
      log("Closing ${closeables.size} autocloseables [$closeables]")
      closeables.forEach { it.value.close() }
   }

   val listeners = resolvedTestListeners() + Project.testListeners()
   listeners.forEach { it.afterSpec(this) }
   this
}

/**
 * Returns the runtime resolved [TestCaseExtension]s applicable for this [TestCase].
 * Those are extensions registered on the test case's own config, those registered
 * on the spec instance, and those registered at the project level.
 */
fun TestCase.extensions(): List<TestCaseExtension> {
   return config.extensions +
      spec.resolvedExtensions().filterIsInstance<TestCaseExtension>() +
      configuration.testCaseExtensions()
}
