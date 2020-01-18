package io.kotest.runner.jvm.spec

import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.resolvedIsolationMode
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.instantiateSpec
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Handles the execution of a single [SpecConfiguration] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param listener a listener that is notified of events in the spec lifecycle
 */
class SpecExecutor(private val listener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   /**
    * Executes the given [SpecConfiguration] and returns true if the spec returned normally.
    */
   suspend fun execute(kclass: KClass<out SpecConfiguration>) {
      logger.trace("Executing spec $kclass")
      notifySpecStarted(kclass)
         .flatMap { notifyPrepareSpec(kclass) }
         .flatMap { createInstance(kclass) }
         .flatMap { runTests(it) }
         .flatMap { notifyFinalizeSpec(kclass, it) }
         .fold({ notifySpecFinished(kclass, it, emptyMap()) }, { notifySpecFinished(kclass, null, it) })
   }

   /**
    * Notifies the [TestEngineListener] that we are about to start execution of a [SpecConfiguration].
    * This is called once per spec regardless of the number of instantiation events.
    */
   private fun notifySpecStarted(kclass: KClass<out SpecConfiguration>) = Try {
      logger.trace("Executing engine listener callback:specStarted for:${kclass}")
      listener.specStarted(kclass)
   }

   /**
    * Notifies the [TestEngineListener] that we have finished the execution of a [SpecConfiguration].
    * This is called once per spec regardless of the number of instantiation events.
    */
   private fun notifySpecFinished(
      kclass: KClass<out SpecConfiguration>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) = Try {
      logger.trace("Executing engine listener callback:specFinished for:${kclass}")
      listener.specFinished(kclass, t, results)
   }

   /**
    * Creates an instance of the supplied [SpecConfiguration] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out SpecConfiguration>): Try<SpecConfiguration> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }

   private suspend fun runTests(spec: SpecConfiguration): Try<Map<TestCase, TestResult>> {
      val mode = spec.resolvedIsolationMode()
      val runner = mode.runner()
      return runner.execute(spec)
   }

   // each runner must get a single-threaded executor, which is used to invoke
   // listeners/extensions and the test itself when testcase.config.threads=1
   // otherwise, the listeners and the tests can be run on seperate threads,
   // which is undesirable in some situations, see
   // https://github.com/kotlintest/kotlintest/issues/447
   private fun IsolationMode.runner(): SpecRunner = when (this) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener)
      else -> SingleInstanceSpecRunner(listener)
      // todo
      // IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
      // IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
   }

   /**
    * Notifies the user listeners that a new [SpecConfiguration] is starting.
    * This is only invoked once per spec class, regardless of the number of invocations.
    */
   private fun notifyPrepareSpec(kclass: KClass<out SpecConfiguration>): Try<Unit> = Try {
      logger.trace("Executing notifyPrepareSpec")
      Project.testListeners().forEach {
         it.prepareSpec(kclass)
      }
   }

   /**
    * Notifies the user listeners that a [SpecConfiguration] has finished all instances.
    * This is only invoked once per spec class, regardless of the number of invocations.
    */
   private fun notifyFinalizeSpec(
      kclass: KClass<out SpecConfiguration>,
      results: Map<TestCase, TestResult>
   ): Try<Map<TestCase, TestResult>> = Try {
      logger.trace("Executing notifyAfterSpec")
      Project.testListeners().forEach {
         it.finalizeSpec(kclass, results)
      }
      results
   }
}
