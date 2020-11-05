package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

interface SpecLauncher {
   suspend fun submit(executor: SpecExecutor, specs: List<KClass<out Spec>>)
}

object SequentialSpecLauncher : SpecLauncher {
   override suspend fun submit(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      log("SequentialSpecLauncher: Launching ${specs.size} sequentially")
      specs.forEach {
         executor.execute(it)
      }
   }
}

/**
 * Executes the given set of specs concurrently by launching each spec in its own coroutine.
 *
 * Each coroutine is launched on a single threaded dispatcher to ensure that all tests inside that coroutine
 * execute on the same thread without requiring tests to be thread aware. The [threads] parameter determines
 * how many such dispatchers are created, and thus how many actual threads are created.
 *
 * The dispatcher allocated to a particular spec is selected in a round robin fashion.
 *
 * This method will suspend until all specs have completed.
 */
class ConcurrentSpecLauncher(private val threads: Int) : SpecLauncher {

   override suspend fun submit(executor: SpecExecutor, specs: List<KClass<out Spec>>) {
      log("ConcurrentSpecLauncher: Launching ${specs.size} spec(s) using $threads dispatcher(s)")

      var index = 0
      val executors = List(threads) { Executors.newSingleThreadExecutor() }
      val dispatchers = executors.map { it.asCoroutineDispatcher() }

      val errors = mutableListOf<Throwable>()

      coroutineScope { // we want to suspend until all specs have completed
         specs.forEach { spec ->
            // round robin picking a dispatcher
            val dispatcher = dispatchers[index++ % threads]
            log("ConcurrentSpecLauncher: Launching coroutine for spec [$spec] with dispatcher [$dispatcher]")
            launch(dispatcher) {
               try {
                  executor.execute(spec)
               } catch (t: Throwable) {
                  log("ConcurrentSpecLauncher: Unhandled error during spec execution [$spec] [$t]")
                  errors.add(t)
               }
            }
         }
      }
      log("ConcurrentSpecLauncher: All specs have completed")

      executors.forEach { it.shutdown() }
      log("ConcurrentSpecLauncher: Waiting for $threads executor(s) to terminate")
      try {
         executors.forEach { it.awaitTermination(1, TimeUnit.MINUTES) }
      } catch (e: InterruptedException) {
         log("ConcurrentSpecLauncher: Interrupted while waiting for dispatcher to terminate", e)
         errors.add(e)
      }

      if (errors.isNotEmpty()) {
         log("ConcurrentSpecLauncher: Unhandled errors in spec execution $errors")
         error("$errors")
      }
   }
}
