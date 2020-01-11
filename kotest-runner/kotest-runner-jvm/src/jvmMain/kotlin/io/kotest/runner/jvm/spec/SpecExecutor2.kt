package io.kotest.runner.jvm.spec

import io.kotest.Project
import io.kotest.core.IsolationMode
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.TopLevelTests
import io.kotest.fp.Try
import io.kotest.fp.getOrElse
import io.kotest.fp.orElse
import io.kotest.fp.toOption
import io.kotest.internal.orderedRootTests
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

/**
 * Handles the execution of a single [SpecConfiguration] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 *
 * @param engineListener a listener that is notified of events in the spec lifecycle
 */
class SpecExecutor2(
   private val engineListener: TestEngineListener
) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   suspend fun execute(spec: SpecConfiguration) = Try {
      logger.trace("Executing spec $spec")
      beforeSpec(spec)
         .flatMap { resolveRootTests(spec) }
         .flatMap { runTests(spec, it) }
         .fold({ afterSpec(spec, it, emptyMap()) }, { afterSpec(spec, null, it) })
   }

   private suspend fun beforeSpec(spec: SpecConfiguration) = Try {
      logger.trace("beforeSpec $spec")

      logger.trace("Executing engine listener 'executionStarted' for ${spec::class}")
      engineListener.specStarted(spec::class)

      logger.trace("Executing user listeners for beforeSpec")
      spec.beforeAlls.forEach { it.invoke() }
      val userListeners = Project.listeners() // listOf(spec) + spec.listenerInstances + Project.listeners()
      userListeners.forEach { _ ->
         // it.beforeSpecStarted(spec::class.description(), spec)
         //  it.beforeSpecClass(spec, tests.tests)
      }

      logger.trace("Completed beforeSpec $spec")
   }

   private suspend fun afterSpec(spec: SpecConfiguration, t: Throwable?, results: Map<TestCase, TestResult>) = Try {
      logger.trace("afterSpec $spec [$t]")

      logger.trace("Executing user listeners for afterSpec")
      spec.afterAlls.forEach { it.invoke(results) }
      val userListeners = Project.listeners() // listOf(spec) + spec.listenerInstances + Project.listeners()
      userListeners.forEach {
         it.afterSpecClass(spec, results)
         @Suppress("DEPRECATION")
         it.afterSpecCompleted(spec::class.description(), spec)
      }

      logger.trace("Executing engine listener 'executionFinished' for ${spec::class}")
      engineListener.specFinished(spec::class, t, results)

      logger.trace("Completed afterSpec $spec")
   }

   private fun resolveRootTests(spec: SpecConfiguration): Try<TopLevelTests> = Try {
      val tests = orderedRootTests(spec)
      logger.trace("Discovered top level tests $tests for spec $spec")
      tests
   }

   private suspend fun runTests(spec: SpecConfiguration, tests: TopLevelTests): Try<Map<TestCase, TestResult>> = Try {
      val runner = runner(spec)
      runner.execute(spec, tests)
      emptyMap<TestCase, TestResult>()
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
