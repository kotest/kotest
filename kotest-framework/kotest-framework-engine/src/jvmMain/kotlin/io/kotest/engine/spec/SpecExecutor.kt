package io.kotest.engine.spec

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.resolvedConcurrentTests
import io.kotest.engine.concurrency.resolvedThreads
import io.kotest.engine.events.Extensions
import io.kotest.engine.events.Notifications
import io.kotest.engine.extensions.resolvedSpecInterceptors
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.runners.ConcurrentInstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerTestSpecRunner
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.engine.test.scheduler.ConcurrentTestScheduler
import io.kotest.engine.test.scheduler.SequentialTestScheduler
import io.kotest.engine.test.status.isEnabled
import io.kotest.fp.Try
import io.kotest.fp.flatten
import io.kotest.fp.success
import io.kotest.mpp.log
import kotlin.math.max
import kotlin.reflect.KClass

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param listener a listener that is notified of events in the test lifecycle
 */
class SpecExecutor(private val listener: TestEngineListener) {

   private val notifications = Notifications(listener)
   private val extensions = Extensions(configuration)

   suspend fun execute(kclass: KClass<out Spec>) {
      log { "SpecExecutor execute [$kclass]" }
      notifications.specStarted(kclass)
         .flatMap { createInstance(kclass) }
         .flatMap { runTestsIfAtLeastOneEnabled(it) }
         .fold(
            { notifications.specFinished(kclass, it, emptyMap()) },
            { notifications.specFinished(kclass, null, it) }
         )
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the constructor extensions,
    * and notifies of the instantiation event.
    */
   private suspend fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      createAndInitializeSpec(kclass)
         .onFailure { notifications.specInstantiationError(kclass, it) }
         .flatMap { spec -> extensions.specInitialize(spec).map { spec } }
         .flatMap { spec -> notifications.specInstantiated(spec).map { spec } }

   /**
    * The root tests on this spec are retrieved, and if none are active, then no
    * execution step takes place. Otherwise, if at least one active, the [runTests]
    * function is invoked.
    */
   private suspend fun runTestsIfAtLeastOneEnabled(spec: Spec): Try<Map<TestCase, TestResult>> {
      log { "runTestsIfAtLeastOneActive [$spec]" }
      val roots = spec.materializeAndOrderRootTests()
      val active = roots.any { it.testCase.isEnabled().isEnabled }

      if (!active) {
         val results = roots.associate { it.testCase to TestResult.ignored(it.testCase.isEnabled()) }
         notifications.specSkipped(spec, results)
         extensions.specFinalize(spec)
      }

      return if (active) runTests(spec) else emptyMap<TestCase, TestResult>().success()
   }

   /**
    * Runs the tests in this spec by delegation to a [SpecRunner].
    *
    * Before the tests are executed we invoke any spec extensions to intercept this spec.
    */
   private suspend fun runTests(spec: Spec): Try<Map<TestCase, TestResult>> {
      var results: Try<Map<TestCase, TestResult>> = emptyMap<TestCase, TestResult>().success()

      // the terminal case after all (if any) extensions have been invoked
      val run: suspend () -> Unit = suspend {
         val runner = runner(spec)
         log { "SpecExecutor: Using runner $runner" }
         extensions.beforeSpec(spec).getOrThrow()
         results = runner.execute(spec)
         extensions.afterSpec(spec).getOrThrow()
         extensions.specFinalize(spec)
      }

      val interceptors = spec.resolvedSpecInterceptors()
      log { "SpecExecutor: Intercepting spec with ${interceptors.size} extensions [$interceptors]" }
      return Try { interceptSpec(spec, interceptors, run) }
         .map { results }
         .flatten()
   }

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecInterceptExtension>,
      run: suspend () -> Unit
   ) {
      when {
         remaining.isEmpty() -> run()
         else -> {
            remaining.first().intercept(spec::class) {
               remaining.first().intercept(spec) {
                  interceptSpec(spec, remaining.drop(1), run)
               }
            }
         }
      }
   }

   private fun Spec.resolvedIsolationMode() =
      this.isolationMode() ?: this.isolationMode ?: configuration.isolationMode

   private fun runner(spec: Spec): SpecRunner {

      val scheduler = when (val concurrentTests = spec.resolvedConcurrentTests()) {
         Configuration.Sequential -> SequentialTestScheduler
         else -> ConcurrentTestScheduler(max(1, concurrentTests))
      }

      return when (spec.resolvedIsolationMode()) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener, scheduler)
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener, scheduler)
         IsolationMode.InstancePerLeaf -> when (val threads = spec.resolvedThreads()) {
            null, 0, 1 -> InstancePerLeafSpecRunner(listener, scheduler)
            else -> ConcurrentInstancePerLeafSpecRunner(listener, threads)
         }
      }
   }
}
