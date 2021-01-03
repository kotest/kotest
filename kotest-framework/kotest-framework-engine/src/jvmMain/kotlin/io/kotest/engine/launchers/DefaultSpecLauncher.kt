package io.kotest.engine.launchers

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.internal.isIsolate
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.reflect.KClass

/**
 * A [SpecLauncher] that uses the constructor parameter maxConcurrent to determine the level
 * of concurrency for specs. Additionally, it will recognize the [Isolate] and [DoNotParallelize]
 * annotations to restrict which specs are concurrent.
 *
 * @param maxConcurrent The maximum number of concurrent coroutines.
 * @param factory a [CoroutineDispatcherFactory] used to allocate CoroutineDispatchers to specs.
 */
@ExperimentalKotest
class DefaultSpecLauncher(
   private val maxConcurrent: Int,
   private val factory: CoroutineDispatcherFactory
) : SpecLauncher {

   private val semaphore = Semaphore(maxConcurrent).apply {
      log("DefaultSpecLauncher: Will use $maxConcurrent permits")
   }

   override suspend fun launch(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      log("DefaultSpecLauncher: Launching ${specs.size} specs")
      when {
         // if we are launching specs concurrently, then we partition the specs into those which
         // can run concurrently (default) and those which cannot (see @Isolated)
         maxConcurrent > 1 -> {
            val (sequential, concurrent) = specs.partition { it.isIsolate() }
            log("DefaultSpecLauncher: Split specs based on annotations [$sequential isolated]")
            concurrent(executor, concurrent)
            sequential(executor, sequential)
         }
         // when not in concurrent mode, all specs are launched sequentially
         else ->  sequential(executor, specs)
      }
      log("DefaultSpecLauncher: All specs have completed")
   }

   private suspend fun sequential(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      specs.forEach { spec ->
         coroutineScope {
            launch(factory.dispatcherFor(spec)) {
               executor.execute(spec)
            }.invokeOnCompletion { factory.complete(spec) }
         }
      }
   }

   private suspend fun concurrent(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      coroutineScope {
         specs.forEach { spec ->
            semaphore.withPermit {
               log("DefaultSpecLauncher: Acquired permit for $spec")

               val dispatcher = factory.dispatcherFor(spec)
               log("DefaultSpecLauncher: Launching coroutine for spec [$spec] with dispatcher [$dispatcher]")

                launch(dispatcher) {
                  try {
                     executor.execute(spec)
                  } catch (t: Throwable) {
                     log("DefaultSpecLauncher: Unhandled error during spec execution [$spec] [$t]")
                     throw t
                  }
               }.invokeOnCompletion { factory.complete(spec) }
            }
         }
      }
      factory.stop()
   }
}
