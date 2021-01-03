package io.kotest.engine.launchers

import io.kotest.core.spec.Spec
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * This implementation of [SpecLauncher] will launch all specs consecutively.
 *
 * @param factory a [CoroutineDispatcherFactory] used to allocate dispatchers for specs.
 */
class SequentialSpecLauncher(private val factory: CoroutineDispatcherFactory) : SpecLauncher {
   override suspend fun launch(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      log("SequentialSpecLauncher: Launching ${specs.size} sequentially")
      specs.forEach { spec ->
         coroutineScope {
            launch(factory.dispatcherFor(spec)) {
               executor.execute(spec)
            }
         }
      }
   }
}
