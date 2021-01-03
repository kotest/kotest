package io.kotest.engine.launchers

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
 * A [SpecLauncher] that launches specs concurrently.
 *
 * @param maxConcurrent The maximum number of coroutines to launch. Uses a semaphore to limit.
 * @param factory a [CoroutineDispatcherFactory] used to allocate dispatchers to specs.
 */
class ConcurrentSpecLauncher(
   maxConcurrent: Int,
   private val factory: CoroutineDispatcherFactory
) : SpecLauncher {

   private val semaphore = Semaphore(maxConcurrent)

   override suspend fun launch(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      coroutineScope {
         specs.forEach { spec ->
            semaphore.withPermit {
               val dispatcher = factory.dispatcherFor(spec)
               log("ConcurrentSpecLauncher: Launching coroutine for spec [$spec] with dispatcher [$dispatcher]")
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
      }
      log("ConcurrentSpecLauncher: All specs have completed")
   }
}
