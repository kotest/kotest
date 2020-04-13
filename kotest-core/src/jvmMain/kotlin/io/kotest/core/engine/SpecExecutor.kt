package io.kotest.core.engine

import io.kotest.mpp.log
import io.kotest.core.config.Project
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.resolvedExtensions
import io.kotest.core.spec.resolvedIsolationMode
import io.kotest.core.spec.style.TestBuilders
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.fp.flatten
import io.kotest.fp.success
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

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
         .flatMap { runTests(it) }
         .flatMap { checkClosedTestCases(it) }
         .flatMap { invokeFinalizeSpecListeners(kclass, it) }
         .fold(
            { notifySpecFinished(kclass, it, emptyMap()) },
            { notifySpecFinished(kclass, null, it) }
         )
   }

   private fun checkClosedTestCases(results: Map<TestCase, TestResult>): Try<Map<TestCase, TestResult>> {
      return if (TestBuilders.state == null) results.success() else Try.Failure(AssertionError("Incorrect usage of DSL"))
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

   private fun notifySpecInstantiationError(kclass: KClass<out Spec>, t: Throwable) = Try {
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
    * Runs the tests in this spec by delegation to a [SpecRunner].
    * Before the tests are executed we invoke any spec extensions to intercept this spec.
    */
   private suspend fun runTests(spec: Spec): Try<Map<TestCase, TestResult>> {

      val extensions = spec.resolvedExtensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
      var results: Try<Map<TestCase, TestResult>> = emptyMap<TestCase, TestResult>().success()

      // the terminal case after all (if any) extensions have been invoked
      val run: suspend () -> Unit = suspend {
         val mode = spec.resolvedIsolationMode()
         val runner = mode.runner()
         results = runner.execute(spec)
      }

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

   @OptIn(ExperimentalTime::class)
   private fun IsolationMode.runner(): SpecRunner = when (this) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener)
      IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener)
      IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(listener) // topo restore per leaf
   }

   /**
    * Notifies the user listeners that a new [Spec] is starting.
    * This is only invoked once per spec class, regardless of the number of invocations.
    * If this errors then no further callbacks or tests will be executed.
    */
   private suspend fun invokePrepareSpecListeners(kclass: KClass<out Spec>): Try<Unit> = Try {
      log("Executing notifyPrepareSpec")
      Project.testListeners().forEach {
         it.prepareSpec(kclass)
      }
   }

   /**
    * Notifies the user listeners that a [Spec] has finished completed.
    */
   private suspend fun invokeFinalizeSpecListeners(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>
   ): Try<Map<TestCase, TestResult>> = Try {
      log("Executing notifyFinalizeSpec")
      Project.testListeners().forEach {
         it.finalizeSpec(kclass, results)
      }
      results
   }
}
