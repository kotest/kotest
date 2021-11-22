package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
@ExperimentalKotest
internal abstract class SpecRunner(
   val listener: TestEngineListener,
   private val scheduler: TestScheduler,
   private val configuration: ProjectConfiguration,
) {

   protected val materializer = Materializer(configuration)
   private val logger = Logger(SingleInstanceSpecRunner::class)

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise, returns the results for the tests in that spec.
    */
   abstract suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>>

   /**
    * Executes all the tests in this spec.
    */
   protected suspend fun launch(spec: Spec, run: suspend (TestCase) -> Unit) {
      val rootTests = materializer.materialize(spec)
      logger.log { Pair(spec::class.bestName(), "Launching ${rootTests.size} root tests with launcher $scheduler") }
      scheduler.schedule(run, rootTests)
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   protected suspend fun createInstance(kclass: KClass<out Spec>): Result<Spec> =
      createAndInitializeSpec(kclass, configuration.registry)
}
