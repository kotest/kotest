package io.kotest.runner.jvm.spec

import io.kotest.SpecClass
import io.kotest.core.config.Project
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.resolvedListeners
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.SpecExtension
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

/**
 * The base class for executing all the tests inside a [SpecClass].
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
      val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
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
            remaining.first().intercept(spec) { interceptSpec(spec, rest, afterInterception) }
         }
      }
      spec
   }

   /**
    * Notifies the user listeners that a [SpecConfiguration] is starting.
    * This will be invoked for every instance of a spec.
    */
   protected fun notifyBeforeSpec(spec: SpecConfiguration): Try<SpecConfiguration> = Try {
      logger.trace("Executing listeners beforeSpec")
      val listeners = spec.resolvedListeners()
      listeners.forEach {
         it.beforeSpec(spec)
      }
      spec
   }

   /**
    * Notifies the user listeners that a [SpecConfiguration] has finished.
    * This will be invoked for every instance of a spec.
    */
   protected fun notifyAfterSpec(spec: SpecConfiguration): Try<SpecConfiguration> = Try {
      logger.trace("Executing listeners beforeSpec")
      val listeners = spec.resolvedListeners()
      listeners.forEach {
         it.afterSpec(spec)
      }
      spec
   }
}
