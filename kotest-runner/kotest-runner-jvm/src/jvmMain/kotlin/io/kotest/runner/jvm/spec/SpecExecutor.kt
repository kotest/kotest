package io.kotest.runner.jvm.spec

import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.resolvedIsolationMode
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.instantiateSpec
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param listener a listener that is notified of events in the spec lifecycle
 */
class SpecExecutor(private val listener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   /**
    * Executes the given [Spec] and returns true if the spec returned normally.
    */
   suspend fun execute(kclass: KClass<out Spec>) {
      logger.trace("Executing spec $kclass")
      notifySpecStarted(kclass)
         .flatMap { notifyPrepareSpec(kclass) }
         .flatMap { createInstance(kclass) }
         .flatMap { runTests(it) }
         .flatMap { notifyFinalizeSpec(kclass, it) }
         .fold({ notifySpecFinished(kclass, it, emptyMap()) }, { notifySpecFinished(kclass, null, it) })
   }

   /**
    * Notifies the [TestEngineListener] that we are about to start execution of a [Spec].
    * This is called once per spec regardless of the number of instantiation events.
    */
   private fun notifySpecStarted(kclass: KClass<out Spec>) = Try {
      logger.trace("Executing engine listener callback:specStarted for:${kclass}")
      listener.specStarted(kclass)
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
      logger.trace("Executing engine listener callback:specFinished for:${kclass}")
      listener.specFinished(kclass, t, results)
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }

   private suspend fun runTests(spec: Spec): Try<Map<TestCase, TestResult>> {
      val mode = spec.resolvedIsolationMode()
      val runner = mode.runner()
      return runner.execute(spec)
   }

   private fun IsolationMode.runner(): SpecRunner = when (this) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener)
      IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener)
      IsolationMode.InstancePerLeaf -> SingleInstanceSpecRunner(listener) // topo restore per leaf
   }

   /**
    * Notifies the user listeners that a new [Spec] is starting.
    * This is only invoked once per spec class, regardless of the number of invocations.
    */
   private fun notifyPrepareSpec(kclass: KClass<out Spec>): Try<Unit> = Try {
      logger.trace("Executing notifyPrepareSpec")
      Project.testListeners().forEach {
         it.prepareSpec(kclass)
      }
   }

   /**
    * Notifies the user listeners that a [Spec] has finished all instances.
    * This is only invoked once per spec class, regardless of the number of invocations.
    */
   private fun notifyFinalizeSpec(
       kclass: KClass<out Spec>,
       results: Map<TestCase, TestResult>
   ): Try<Map<TestCase, TestResult>> = Try {
      logger.trace("Executing notifyAfterSpec")
      Project.testListeners().forEach {
         it.finalizeSpec(kclass, results)
      }
      results
   }
}
