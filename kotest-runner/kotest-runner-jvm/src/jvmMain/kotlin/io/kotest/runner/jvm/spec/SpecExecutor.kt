package io.kotest.runner.jvm.spec

import io.kotest.Description
import io.kotest.core.IsolationMode
import io.kotest.core.Project
import io.kotest.core.fp.Try
import io.kotest.core.fp.getOrElse
import io.kotest.core.fp.orElse
import io.kotest.core.fp.toOption
import io.kotest.core.fromSpecClass
import io.kotest.core.specs.Spec
import io.kotest.core.orderedRootTests
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Handles the execution of a single [Spec] class.
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

   fun execute(spec: Spec) = Try {
      withExecutor { listenerExecutor ->

         engineListener.beforeSpec(spec)

         val userListeners = spec.listeners + Project.listeners()

         Try {

            val tests = spec.orderedRootTests()
            logger.trace("Discovered top level tests $tests for spec $spec")

            userListeners.forEach {
               // todo restore listeners               it.beforeSpecClass(spec, tests.tests)
            }

            val runner = runner(spec, listenerExecutor, scheduler)
            val results = runner.execute(spec, tests)

            userListeners.forEach {
               // todo restore listeners               it.afterSpecClass(spec, results)
            }

         }.fold(
            {
               logger.trace("Completing spec ${Description.fromSpecClass(spec::class)} with error $it")
               // todo restore listeners     engineListener.afterSpecClass(spec.javaClass.kotlin, it)
            },
            {
               logger.trace("Completing spec ${Description.fromSpecClass(spec::class)} with success")
               // todo restore listeners    engineListener.afterSpecClass(spec.javaClass.kotlin, null)
            }
         )
      }
   }

   // each runner must get a single-threaded executor, which is used to invoke
   // listeners/extensions and the test itself when testcase.config.threads=1
   // otherwise, the listeners and the tests can be run on seperate threads,
   // which is undesirable in some situations, see
   // https://github.com/kotlintest/kotlintest/issues/447
   private fun runner(
      spec: Spec,
      listenerExecutor: ExecutorService,
      scheduler: ScheduledExecutorService
   ): SpecRunner {
      val mode = spec.isolationMode.toOption()
         .orElse { Project.isolationMode().toOption() }
         .getOrElse { IsolationMode.SingleInstance }
      return when (mode) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(engineListener, listenerExecutor, scheduler)
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
         IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
      }
   }
}
