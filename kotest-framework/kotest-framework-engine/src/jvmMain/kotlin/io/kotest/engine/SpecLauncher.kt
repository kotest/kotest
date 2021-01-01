package io.kotest.engine

import io.kotest.core.config.LaunchMode
import io.kotest.core.config.configuration
import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.core.internal.isIsolate
import io.kotest.core.spec.Spec
import io.kotest.engine.config.factory
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * Performs the execution of specs.
 */
interface SpecLauncher {

   /**
    * Given the list of specs, this launcher should execute each of those specs.
    * Spec and test lifecycle events should be reported back to the test engine listener.
    *
    * This method should suspend until all specs have completed.
    */
   suspend fun submit(listener: TestEngineListener, specs: List<KClass<out Spec>>)
}

/**
 * The default [SpecLauncher] which supports concurrent dispatch of specs.
 *
 * Each coroutine is launched on a single threaded dispatcher to ensure that all tests
 * inside that coroutine execute on the same thread without requiring tests to be thread
 * aware. The [CoroutineDispatcherFactoryExtension] assigns dispatchers to specs.
 */
object DefaultSpecLauncher : SpecLauncher {

   override suspend fun submit(listener: TestEngineListener, specs: List<KClass<out Spec>>) {
      // if we are launching specs concurrently, then we partition the specs into those which
      // can run concurrently (default) and those which cannot (see @Isolated)
      val (sequential, parallel) = when {
         isConcurrent() -> specs.partition { it.isIsolate() }
         else -> Pair(specs, emptyList())
      }

      log("DefaultSpecLauncher: Partitioned specs into ${parallel.size} parallel and ${sequential.size} sequential")

      if (parallel.isNotEmpty()) concurrent(parallel, listener, factory.value)
      if (sequential.isNotEmpty()) sequential(sequential, listener, factory.value)
   }

   /**
    * Returns true if we should concurrently launch the specs.
    */
   private fun isConcurrent() = when (configuration.specLaunchMode) {
      // when spec launch mode is not specified, we use the value of the parallelism to infer
      null -> configuration.parallelism > 1
      LaunchMode.Consecutive -> false  // explicitly deactivates spec concurrency
      LaunchMode.Concurrent -> true // explicitly activated spec concurrency
   }

   private suspend fun sequential(
      specs: List<KClass<out Spec>>,
      listener: TestEngineListener,
      factory: CoroutineDispatcherFactoryExtension
   ) {
      val executor = SpecExecutor(listener)
      log("DefaultSpecLauncher: Launching ${specs.size} sequentially")
      specs.forEach { spec ->
         coroutineScope {
            launch(factory.dispatcherFor(spec)) {
               executor.execute(spec)
            }
         }
      }
   }

   private suspend fun concurrent(
      specs: List<KClass<out Spec>>,
      listener: TestEngineListener,
      factory: CoroutineDispatcherFactoryExtension
   ) {
      val executor = SpecExecutor(listener)
      log("DefaultSpecLauncher: Launching ${specs.size} spec(s) using $factory dispatcher(s)")
      coroutineScope { // we want to suspend until all specs have completed
         specs.forEach { spec ->
            val dispatcher = factory.dispatcherFor(spec)
            log("DefaultSpecLauncher: Launching coroutine for spec [$spec] with dispatcher [$dispatcher]")
            launch(dispatcher) {
               try {
                  executor.execute(spec)
               } catch (t: Throwable) {
                  log("ConcurrentSpecLauncher: Unhandled error during spec execution [$spec] [$t]")
                  throw t
               }
            }
         }
      }
      log("DefaultSpecLauncher: All specs have completed")
   }
}
