package io.kotest.engine.spec

import io.kotest.mpp.log
import io.kotest.core.config.configuration
import io.kotest.core.config.testListeners
import io.kotest.engine.runners.ConcurrentInstancePerLeafSpecRunner
import io.kotest.engine.runners.InstancePerLeafSpecRunner
import io.kotest.engine.runners.InstancePerTestSpecRunner
import io.kotest.engine.runners.SingleInstanceSpecRunner
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.style.scopes.DslState
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.resolvedSpecExtensions
import io.kotest.core.internal.resolvedThreads
import io.kotest.engine.instantiateSpec
import io.kotest.core.internal.isActive
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.fp.Try
import io.kotest.fp.flatten
import io.kotest.fp.success
import kotlin.reflect.KClass

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param listener a listener that is notified of events in the spec lifecycle
 */
class SpecExecutor(private val listener: TestEngineListener) {

   /**
    * Executes the given [Spec].
    */
   suspend fun execute(kclass: KClass<out Spec>) {
      log("Executing spec $kclass")
      notifySpecStarted(kclass)
         .flatMap { invokePrepareSpecListeners(kclass) }
         .flatMap { createInstance(kclass) }
         .flatMap { runTestsIfAtLeastOneActive(it) }
         .flatMap { checkClosedTestCases(it) }
         .flatMap { invokeFinalizeSpecListeners(kclass, it) }
         .fold(
            { notifySpecFinished(kclass, it, emptyMap()) },
            { notifySpecFinished(kclass, null, it) }
         )
   }

   private fun checkClosedTestCases(results: Map<TestCase, TestResult>): Try<Map<TestCase, TestResult>> {
      return when (val state = DslState.state) {
         null -> results.success()
         else -> {
            DslState.state = null
            Try.Failure(AssertionError(state))
         }
      }
   }

   /**
    * Notifies the [TestEngineListener] that we are about to start execution of a [Spec].
    * This is called only once per spec regardless of the number of instantiation events.
    */
   private fun notifySpecStarted(kclass: KClass<out Spec>) = Try {
      log("Executing engine listener callback:specStarted $kclass")
      listener.specStarted(kclass)
   }

   private fun notifySpecInstantiated(spec: Spec) = Try {
      log("Executing engine listener callback:specInstantiated spec:$spec")
      listener.specInstantiated(spec)
   }

   private fun notifySpecInstantiationError(kclass: KClass<out Spec>, t: Throwable) =
      Try {
         t.printStackTrace()
         log("Executing engine listener callback:specInstantiationError $kclass error:$t")
         listener.specInstantiationError(kclass, t)
      }

   /**
    * Notifies the [TestEngineListener] that we have finished the execution of a [Spec].
    * This is called once per spec regardless of the number of instantiation events.
    */
   private fun notifySpecFinished(
      kclass: KClass<out Spec>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) = Try {
      t?.printStackTrace()
      log("Executing engine listener callback:specFinished $kclass")
      listener.specFinished(kclass, t, results)
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      instantiateSpec(kclass)
         .onFailure { notifySpecInstantiationError(kclass, it) }
         .onSuccess { notifySpecInstantiated(it) }

   /**
    * The root tests on this spec are retrieved, and if none are active, then no
    * execution step takes place. Otherwise if at least one active, the [runTests]
    * function is invoked.
    */
   private suspend fun runTestsIfAtLeastOneActive(spec: Spec): Try<Map<TestCase, TestResult>> {
      val roots = spec.materializeAndOrderRootTests()
      val active = roots.any { it.testCase.isActive() }
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
            val rest = remaining.drop(1)
            remaining.first().intercept(spec::class) {
               interceptSpec(spec, rest, run)
            }
         }
      }
   }

   private fun Spec.resolvedIsolationMode() =
      this.isolationMode() ?: this.isolationMode ?: this.isolation ?: configuration.isolationMode

   private fun runner(spec: Spec): SpecRunner {
      return when (spec.resolvedIsolationMode()) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener)
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener)
         IsolationMode.InstancePerLeaf -> when (spec.resolvedThreads()) {
            0, 1 -> InstancePerLeafSpecRunner(listener) // topo restore per leaf
            else -> ConcurrentInstancePerLeafSpecRunner(listener, spec.resolvedThreads())
         }
      }
   }

   /**
    * Notifies the user listeners that a new [Spec] is starting.
    * This is only invoked once per spec class, regardless of the number of invocations.
    * If this errors then no further callbacks or tests will be executed.
    */
   private suspend fun invokePrepareSpecListeners(kclass: KClass<out Spec>): Try<Unit> =
      Try {
         // prepareSpec can only be registered at the project level
         // It makes no sense to call prepareSpec after a spec has already been instantiated.
         val listeners = configuration.testListeners()
         log("Notifying ${listeners.size} test listeners of callback 'prepareSpec'")
         listeners.forEach {
            it.prepareSpec(kclass)
         }
         log("'prepareSpec' callbacks complete")
      }

   /**
    * Notifies the user listeners that a [Spec] has finished all tests.
    */
   private suspend fun invokeFinalizeSpecListeners(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>
   ): Try<Map<TestCase, TestResult>> = Try {
      log("Notifying finalizeSpec")
      // finalize spec's can be registered at the project level or using the dsl
      // dsl callbacks are just registered at the project level with a spec class check
      configuration.testListeners().forEach {
         it.finalizeSpec(kclass, results)
      }
      results
   }
}
