package io.kotest.engine.spec

import io.kotest.core.config.LaunchMode
import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.createAndInitializeSpec
import io.kotest.engine.listener.TestEngineListener
import io.kotest.fp.Try
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
   val factory: CoroutineDispatcherFactoryExtension
) {

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise returns the results for the tests in that spec.
    */
   abstract suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>>

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

   protected suspend fun run(
      launchMode: LaunchMode?,
      testCases: Collection<TestCase>,
      run: suspend (TestCase) -> Unit
   ) {
      when (launchMode) {
         LaunchMode.Consecutive -> testCases.forEach { testCase ->
            coroutineScope {
               launch(factory.dispatcherFor(testCase)) {
                  run(testCase)
               }
            }
         }
         else -> coroutineScope {
            testCases.map { testCase ->
               launch(factory.dispatcherFor(testCase)) {
                  run(testCase)
               }
            }
         }
      }
   }
}
