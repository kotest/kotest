package io.kotest.engine.spec

import io.kotest.core.config.configuration
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.launchers.TestLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.fp.Try
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(
   val listener: TestEngineListener,
   val launcher: TestLauncher,
) {

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise returns the results for the tests in that spec.
    */
   abstract suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>>

   /**
    * Executes all the tests in this spec.
    */
   protected suspend fun launch(spec: Spec, run: suspend (TestCase) -> Unit) {
      val rootTests = spec.materializeAndOrderRootTests(ExecutionContext(configuration)).map { it.testCase }
      log { "SingleInstanceSpecRunner: Launching ${rootTests.size} root tests with launcher $launcher" }
      launcher.launch(run, rootTests)
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   protected fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      createAndInitializeSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }

   @Deprecated("Explicit thread mode Will be removed in 4.7")
   protected suspend fun runParallel(threads: Int, testCases: Collection<TestCase>, run: suspend (TestCase) -> Unit) {

      val executor = Executors.newFixedThreadPool(threads, NamedThreadFactory("SpecRunner-%d"))

      val futures = testCases.map { testCase ->
         executor.submit {
            runBlocking {
               run(testCase)
            }
         }
      }
      executor.shutdown()
      log { "Waiting for test case execution to terminate" }

      try {
         executor.awaitTermination(1, TimeUnit.DAYS)
      } catch (t: InterruptedException) {
         log(t) { "Test case execution interrupted" }
         throw t
      }

      //Handle Uncaught Exception in threads or they just be swallowed
      try {
         futures.forEach { it.get() }
      } catch (e: ExecutionException) {
         throw e.cause ?: e
      }
   }
}
