package io.kotest.runner.jvm.spec

import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.resolvedExtensions
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.instantiateSpec
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * The base class for executing all the tests inside a [SpecConfiguration].
 *
 * Each spec can define how tests are isolated from each other, via an [IsolationMode].
 * The implementation for each mode is handled by an instance of [SpecRunner].
 *
 * @param listener provides callbacks on tests as they are executed. These callbacks are used
 * to ultimately feed back into the test engine implementation.
 */
abstract class SpecRunner(val listener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(javaClass)

   abstract suspend fun execute(spec: SpecConfiguration): Try<Map<TestCase, TestResult>>

   suspend fun interceptSpec(spec: SpecConfiguration, afterInterception: suspend () -> Unit): Try<SpecConfiguration> {
      val extensions = spec.resolvedExtensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
      return interceptSpec(spec, extensions, afterInterception)
   }

   private suspend fun interceptSpec(
      spec: SpecConfiguration,
      remaining: List<SpecExtension>,
      afterInterception: suspend () -> Unit
   ): Try<SpecConfiguration> = Try {
      when {
         remaining.isEmpty() -> afterInterception()
         else -> {
            val rest = remaining.drop(1)
            remaining.first().intercept(spec::class) { interceptSpec(spec, rest, afterInterception) }
         }
      }
      spec
   }

   /**
    * Creates an instance of the supplied [SpecConfiguration] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   protected fun createInstance(kclass: KClass<out SpecConfiguration>): Try<SpecConfiguration> =
      instantiateSpec(kclass).onSuccess {
         Try { listener.specInstantiated(it) }
      }.onFailure {
         it.printStackTrace()
         Try { listener.specInstantiationError(kclass, it) }
      }
}
