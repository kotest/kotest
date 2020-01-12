package io.kotest.runner.jvm.spec

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

   suspend fun execute(kclass: KClass<out SpecConfiguration>) = Try {
      logger.trace("Executing spec $kclass")
      notifySpecStarted(kclass)
         .flatMap { createInstance(kclass) }
         .flatMap { runTests(it) }
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
      logger.trace("Executing engine listener 'executionFinished' for ${kclass}")
      listener.specFinished(kclass, t, results)
   }

   /**
    * Creates an instance of the supplied [SpecConfiguration] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out SpecConfiguration>): Try<SpecConfiguration> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specCreated(it) }
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
}
