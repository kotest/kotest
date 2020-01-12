package io.kotest.runner.jvm.spec

import io.kotest.Project
import io.kotest.core.IsolationMode
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.TopLevelTest
import io.kotest.fp.Try
import io.kotest.fp.getOrElse
import io.kotest.fp.orElse
import io.kotest.fp.toOption
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

/**
 * Handles the execution of a single [SpecConfiguration] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param engineListener a listener that is notified of events in the spec lifecycle
 */
class SpecExecutor2(private val engineListener: TestEngineListener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   suspend fun execute(spec: SpecConfiguration) = Try {
      logger.trace("Executing spec $spec")
      notifySpecStarted(spec)
         .flatMap { beforeSpecListeners(spec) }
         .flatMap { spec.materializeRootTests() }
         .flatMap { runTests(spec, it) }
         .flatMap { afterSpec(spec, it) }
         .fold({ notifySpecFinished(spec, it, emptyMap()) }, { notifySpecFinished(spec, null, it) })
   }

   private fun notifySpecStarted(spec: SpecConfiguration) = Try {
      logger.trace("Executing engine listener callback:specStarted for:${spec::class}")
      engineListener.specStarted(spec::class)
   }

   private fun notifySpecFinished(spec: SpecConfiguration, t: Throwable?, results: Map<TestCase, TestResult>) = Try {
      logger.trace("Executing engine listener 'executionFinished' for ${spec::class}")
      engineListener.specFinished(spec::class, t, results)
   }

   private fun beforeSpecListeners(spec: SpecConfiguration) = Try {
      logger.trace("Executing user listeners before spec")
      spec.beforeSpecs.forEach { it.invoke() }
      spec.beforeSpec(spec)
      spec.beforeSpec(spec::class.description(), spec)
      val listeners = Project.listeners() + spec.listeners()
      listeners.forEach {
         it.beforeSpec(spec)
         it.afterSpec(spec)
         it.prepareSpec(spec::class)
      }
      logger.trace("Completed beforeSpec $spec")
   }

   private fun afterSpec(spec: SpecConfiguration, results: Map<TestCase, TestResult>) = Try {
      logger.trace("Executing user listeners after spec")
      spec.afterSpecs.forEach { it.invoke(results) }
      spec.afterSpec(spec)
      val userListeners = Project.listeners() // listOf(spec) + spec.listenerInstances + Project.listeners()
      userListeners.forEach {
         it.finalizeSpec(spec::class, results)
      }
      spec.finalizeSpec(spec, results)
      results
   }

   private suspend fun runTests(
      spec: SpecConfiguration,
      tests: List<TopLevelTest>
   ): Try<Map<TestCase, TestResult>> =
      Try {
         val runner = runner(spec)
         runner.execute(spec, tests)
      }

   // each runner must get a single-threaded executor, which is used to invoke
   // listeners/extensions and the test itself when testcase.config.threads=1
   // otherwise, the listeners and the tests can be run on seperate threads,
   // which is undesirable in some situations, see
   // https://github.com/kotlintest/kotlintest/issues/447
   private fun runner(spec: SpecConfiguration): SpecRunner {
      @Suppress("DEPRECATION")
      val mode = spec.isolationMode().toOption()
         .orElse { Project.isolationMode().toOption() }
         .getOrElse { IsolationMode.SingleInstance }
      return when (mode) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(engineListener)
         else -> SingleInstanceSpecRunner(engineListener)
         // todo
         // IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
         // IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
      }
   }
}
