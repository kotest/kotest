package io.kotest.engine.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.resolvedSpecExtensions
import io.kotest.core.internal.isActive
import io.kotest.core.internal.resolvedThreads
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.NotificationManager
import io.kotest.engine.launchers.testLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.runners.ConcurrentInstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerTestSpecRunner
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.fp.Try
import io.kotest.fp.flatten
import io.kotest.fp.success
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param listener a listener that is notified of events in the test lifecycle
 */
class SpecExecutor(private val listener: TestEngineListener) {

   private val notifications = NotificationManager(listener)

   suspend fun execute(kclass: KClass<out Spec>) {
      log("SpecExecutor execute [$kclass]")
      notifications.specStarted(kclass)
         .flatMap { createInstance(kclass) }
         .flatMap { runTestsIfAtLeastOneActive(it) }
         .fold(
            { notifications.specFinished(kclass, it, emptyMap()) },
            { notifications.specFinished(kclass, null, it) }
         )
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the constructor extensions,
    * and notifies of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      createAndInitializeSpec(kclass)
         .onFailure { notifications.specInstantiationError(kclass, it) }
         .flatMap { spec -> notifications.specInstantiated(spec).map { spec } }

   /**
    * The root tests on this spec are retrieved, and if none are active, then no
    * execution step takes place. Otherwise if at least one active, the [runTests]
    * function is invoked.
    */
   private suspend fun runTestsIfAtLeastOneActive(spec: Spec): Try<Map<TestCase, TestResult>> {
      log("runTestsIfAtLeastOneActive [$spec]")
      val roots = spec.materializeAndOrderRootTests()
      val active = roots.any { it.testCase.isActive().active }
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
         log("SpecExecutor: Using runner $runner")
         results = runner.execute(spec)
      }

      val extensions = spec.resolvedSpecExtensions()
      log("SpecExecutor: Intercepting spec with ${extensions.size} extensions [$extensions]")
      return Try { interceptSpec(spec, extensions, run) }.map { results }.flatten()
   }

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecExtension>,
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
      this.isolationMode() ?: this.isolationMode ?: this.isolation ?: configuration.isolationMode

   private fun runner(spec: Spec): SpecRunner {
      return when (spec.resolvedIsolationMode()) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener, testLauncher(spec))
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener, testLauncher(spec))
         IsolationMode.InstancePerLeaf -> when (val threads = spec.resolvedThreads()) {
            null, 0, 1 -> InstancePerLeafSpecRunner(listener, testLauncher(spec))
            else -> ConcurrentInstancePerLeafSpecRunner(listener, threads)
         }
      }
   }
}
