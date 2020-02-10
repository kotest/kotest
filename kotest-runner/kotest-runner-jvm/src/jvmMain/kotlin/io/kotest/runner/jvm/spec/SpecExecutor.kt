package io.kotest.runner.jvm.spec

import io.kotest.assertions.log
import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.resolvedIsolationMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.fp.flatten
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.instantiateSpec
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
    * Executes the given [Spec] and returns true if the spec returned normally.
    */
   suspend fun execute(kclass: KClass<out Spec>) {
      log("Executing spec $kclass")
      notifySpecStarted(kclass)
         .flatMap { notifyPrepareSpec(kclass) }
         .flatMap { createInstance(kclass) }
         .flatMap { runTests(it) }
         .flatMap { notifyFinalizeSpec(kclass, it) }
         .fold(
            { notifySpecFinished(kclass, it, emptyMap()) },
            { notifySpecFinished(kclass, null, it) }
         )
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

   private suspend fun runTests(spec: Spec): Try<Map<TestCase, TestResult>> = Try {
      val mode = spec.resolvedIsolationMode()
      val runner = mode.runner()
      runner.execute(spec)
   }.flatten()

   @UseExperimental(ExperimentalTime::class)
   private fun IsolationMode.runner(): SpecRunner = when (this) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener)
      IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener)
      IsolationMode.InstancePerLeaf -> InstanceLeafSpecRunner(listener) // topo restore per leaf
   }

   /**
    * Notifies the user listeners that a new [Spec] is starting.
    * This is only invoked once per spec class, regardless of the number of invocations.
    * If this errors then no further callbacks or tests will be executed.
    */
   private suspend fun notifyPrepareSpec(kclass: KClass<out Spec>): Try<Unit> = Try {
      log("Executing notifyPrepareSpec")
      Project.testListeners().forEach {
         it.prepareSpec(kclass)
      }
   }

   /**
    * Notifies the user listeners that a [Spec] has finished all instances.
    * This is only invoked once per spec class, regardless of the number of invocations.
    */
   private suspend fun notifyFinalizeSpec(
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
