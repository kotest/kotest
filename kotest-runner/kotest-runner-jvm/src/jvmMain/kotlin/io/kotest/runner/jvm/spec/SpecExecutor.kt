package io.kotest.runner.jvm.spec

import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.orElse
import arrow.core.toOption
import io.kotest.core.IsolationMode
import io.kotest.Project
import io.kotest.SpecClass
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.internal.orderedRootTests
import io.kotest.listenerInstances
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Handles the execution of a single [SpecClass] class.
 * Delegates to a [SpecRunner] which determines how and when
 * to instantiate fresh specs based on the [IsolationMode] of the spec.
 */
class SpecExecutor(
   private val engineListener: TestEngineListener,
   private val scheduler: ScheduledExecutorService
) {

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

   fun execute(spec: SpecConfiguration) = Try {
      withExecutor { listenerExecutor ->

         //engineListener.beforeSpecClass(spec::class)

         val userListeners = Project.listeners() // listOf(spec) + spec.listenerInstances + Project.listeners()

         Try {

            val tests = orderedRootTests(spec)
            logger.trace("Discovered top level tests $tests for spec $spec")

            userListeners.forEach {
               // it.beforeSpecStarted(spec::class.description(), spec)
               it.beforeSpecClass(spec, tests.tests)
            }

            val runner = runner(spec, listenerExecutor, scheduler)
            val results = runner.execute(spec, tests)

            userListeners.forEach {
               it.afterSpecClass(spec, results)
               // it.afterSpecCompleted(spec::class.description(), spec)
            }

         }.fold(
            {
               logger.trace("Completing spec ${spec::class.description()} with error $it")
               //     engineListener.afterSpecClass(spec.javaClass.kotlin, it)
            },
            {
               logger.trace("Completing spec ${spec::class.description()} with success")
               //     engineListener.afterSpecClass(spec.javaClass.kotlin, null)
            }
         )
      }
      //  todo spec.closeResources()
   }

   // each runner must get a single-threaded executor, which is used to invoke
   // listeners/extensions and the test itself when testcase.config.threads=1
   // otherwise, the listeners and the tests can be run on seperate threads,
   // which is undesirable in some situations, see
   // https://github.com/kotlintest/kotlintest/issues/447
   private fun runner(
      spec: SpecClass,
      listenerExecutor: ExecutorService,
      scheduler: ScheduledExecutorService
   ): SpecRunner {
      val mode = spec.isolationMode().toOption()
         .orElse { Project.isolationMode().toOption() }
         .getOrElse { IsolationMode.SingleInstance }
      return when (mode) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(engineListener, listenerExecutor, scheduler)
         else -> SingleInstanceSpecRunner(engineListener, listenerExecutor, scheduler)
         // todo
        // IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
        // IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
      }
   }
}
