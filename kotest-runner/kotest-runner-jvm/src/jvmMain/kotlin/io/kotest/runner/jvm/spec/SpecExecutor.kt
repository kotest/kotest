//package io.kotest.runner.jvm.spec
//
//import io.kotest.core.IsolationMode
//import io.kotest.Project
//import io.kotest.SpecClass
//import io.kotest.core.description
//import io.kotest.core.spec.SpecConfiguration
//import io.kotest.fp.Try
//import io.kotest.fp.getOrElse
//import io.kotest.fp.orElse
//import io.kotest.fp.toOption
//import io.kotest.internal.orderedRootTests
//import io.kotest.runner.jvm.TestEngineListener
//import org.slf4j.LoggerFactory
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import java.util.concurrent.ScheduledExecutorService
//
///**
// * Handles the execution of a single [SpecClass] class.
// * Delegates to a [SpecRunner] which determines how and when
// * to instantiate fresh specs based on the [IsolationMode] of the spec.
// */
//class SpecExecutor(
//   private val engineListener: TestEngineListener,
//   private val scheduler: ScheduledExecutorService
//) {
//
//   private val logger = LoggerFactory.getLogger(this.javaClass)
//
//   // each spec has it's own "main thread" (courtesy of an executor)
//   // this main thread is always used to execute the before and after callbacks, and also tests
//   // where config has threads = 1 (the default). In tests where threads > 1, then a seperate executor is required.
//
//   private fun withExecutor(thunk: (ExecutorService) -> Unit) {
//      val listenerExecutor = Executors.newSingleThreadExecutor {
//         val t = Thread(it)
//         t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
//            logger.error("Error in executor", e)
//         }
//         t
//      }
//      thunk(listenerExecutor)
//      // only on exiting the spec can the listener executor can be shutdown
//      listenerExecutor.shutdown()
//   }
//
//   private fun beforeSpecClass(spec: SpecConfiguration) = Try {
//      engineListener.specStarted(spec::class)
//   }
//
//   private fun runTests(spec: SpecConfiguration) = Try {
//      val tests = orderedRootTests(spec)
//      logger.trace("Discovered top level tests $tests for spec $spec")
//
//      logger.trace("Executing user listeners for before spec")
//
//      val userListeners = Project.listeners() // listOf(spec) + spec.listenerInstances + Project.listeners()
//
//      userListeners.forEach {
//         // it.beforeSpecStarted(spec::class.description(), spec)
//         it.beforeSpecClass(spec, tests.tests)
//      }
//
//      val runner = runner(spec)
////      val results = runner.execute(spec, tests)
//      TODO()
//
//      logger.trace("Executing user listeners for after spec")
//
//      userListeners.forEach {
//         it.afterSpecClass(spec, results)
//         @Suppress("DEPRECATION")
//         it.afterSpecCompleted(spec::class.description(), spec)
//      }
//   }
//
//   fun execute(spec: SpecConfiguration) = Try {
//      logger.trace("specspecspecspec$spec")
//
//      withExecutor { listenerExecutor ->
//
//         beforeSpecClass(spec)
////            .flatMap { runTests(spec, listenerExecutor) }
//            .fold(
//               {
//                  logger.debug("Completing spec ${spec::class.description()} with error $it")
//                  //engineListener.executionFinished(spec::class, it)
//               },
//               {
//                  logger.debug("Completing spec ${spec::class.description()} with success")
//                  //  engineListener.executionFinished(spec::class, null)
//               }
//            )
//      }
//   }
//
//   // each runner must get a single-threaded executor, which is used to invoke
//   // listeners/extensions and the test itself when testcase.config.threads=1
//   // otherwise, the listeners and the tests can be run on seperate threads,
//   // which is undesirable in some situations, see
//   // https://github.com/kotlintest/kotlintest/issues/447
//   private fun runner(
//      spec: SpecConfiguration
//   ): SpecRunner {
//      @Suppress("DEPRECATION")
//      val mode = spec.isolationMode().toOption()
//         .orElse { Project.isolationMode().toOption() }
//         .getOrElse { IsolationMode.SingleInstance }
//      return when (mode) {
//         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(engineListener)
//         else -> SingleInstanceSpecRunner(engineListener)
//         // todo
//         // IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(engineListener, listenerExecutor, scheduler)
//         // IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(engineListener, listenerExecutor, scheduler)
//      }
//   }
//}
