package io.kotest.core.engine

import io.kotest.assertions.log
import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.resolvedExtensions
import io.kotest.fp.Try
import kotlin.reflect.KClass

/**
 * The base class for executing all the tests inside a [Spec].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 * The implementation for each mode is handled by an instance of [SpecRunner].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(val listener: TestEngineListener) {

   /**
    * Executes all the tests in this spec, returning a Failure if there was an exception in a listener
    * or class initializer. Otherwise returns the results for the tests in that spec.
    */
   abstract suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>>

   suspend fun interceptSpec(spec: Spec, afterInterception: suspend () -> Unit) {
      log("Intercepting spec $spec")
      val extensions = spec.resolvedExtensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
      interceptSpec(spec, extensions, afterInterception)
   }

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecExtension>,
      afterInterception: suspend () -> Unit
   ) {
      when {
         remaining.isEmpty() -> afterInterception()
         else -> {
            val rest = remaining.drop(1)
            remaining.first().intercept(spec::class) { interceptSpec(spec, rest, afterInterception) }
         }
      }
   }

   /**
    * Creates an instance of the supplied [Spec] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   protected fun createInstance(kclass: KClass<out Spec>): Try<Spec> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }
}
