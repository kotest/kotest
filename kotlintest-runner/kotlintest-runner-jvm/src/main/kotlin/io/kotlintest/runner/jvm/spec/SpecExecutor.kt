package io.kotlintest.runner.jvm.spec

import arrow.core.Try
import io.kotlintest.IsolationMode
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.internal.topLevelTests
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Handles the execution of a single [Spec] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 */
class SpecExecutor(private val engineListener: TestEngineListener,
                   private val scheduler: ScheduledExecutorService) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  // each spec has it's own "main thread" (courtesy of an executor)
  // this main thread is always used to execute the before and after callbacks, and also tests
  // where config has threads = 1 (the default). In tests where threads > 1, then a seperate executor is required.

  private fun withExecutor(thunk: (ExecutorService) -> Unit) {
    val listenerExecutor = Executors.newSingleThreadExecutor()
    thunk(listenerExecutor)
    // only on exiting the spec can the listener executor can be shutdown
    listenerExecutor.shutdown()
  }

  fun execute(spec: Spec) = Try {
    withExecutor { listenerExecutor ->

      engineListener.beforeSpecClass(spec.description(), spec::class)

      val userListeners = listOf(spec) + spec.listeners() + Project.listeners()

      Try {

        val tests = topLevelTests(spec)
        logger.trace("Discovered top level tests $tests for spec $spec")

        userListeners.forEach {
          it.beforeSpecStarted(spec.description(), spec)
          it.beforeSpecClass(spec, tests)
        }

        val runner = runner(spec, listenerExecutor, scheduler)
        val results = runner.execute(spec, tests)

        userListeners.forEach {
          it.afterSpecClass(spec, results)
          it.afterSpecCompleted(spec.description(), spec)
        }

      }.fold(
          {
            logger.trace("Completing spec ${spec.description()} with error $it")
            engineListener.afterSpecClass(spec.description(), spec.javaClass.kotlin, it)
          },
          {
            logger.trace("Completing spec ${spec.description()} with success")
            engineListener.afterSpecClass(spec.description(), spec.javaClass.kotlin, null)
          }
      )
    }
    spec.closeResources()
  }

  // each runner must get a single-threaded executor, which is used to invoke
  // listeners/extensions and the test itself when testcase.config.threads=1
  // otherwise, the listeners and the tests can be run on seperate threads,
  // which is undesirable in some situations, see
  // https://github.com/kotlintest/kotlintest/issues/447
  private fun runner(spec: Spec, listenerExecutor: ExecutorService, scheduler: ScheduledExecutorService): SpecRunner {
    return when (spec.isolationMode()) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(engineListener, listenerExecutor, scheduler)
      IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
      IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
      null -> when {
        spec.isInstancePerTest() -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
        else -> SingleInstanceSpecRunner(engineListener, listenerExecutor, scheduler)
      }
    }
  }
}