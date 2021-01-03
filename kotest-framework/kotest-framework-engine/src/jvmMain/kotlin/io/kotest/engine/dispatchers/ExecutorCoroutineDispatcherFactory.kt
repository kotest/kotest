package io.kotest.engine.dispatchers

import io.kotest.core.config.configuration
import io.kotest.core.internal.resolvedThreads
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.mpp.NamedThreadFactory
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.reflect.KClass

data class ExecutorCoroutineDispatcher(
   val executor: ExecutorService,
   val coroutineDispatcher: CoroutineDispatcher,
)

/**
 * A [CoroutineDispatcherFactory] that will create single threaded dispatchers based on the parallelism
 * count specified in configuration. This factory guarantees dispatcher affinity - that is a particular
 * spec and all the tests in it will receive the same dispatcher, unless the spec overrides the
 * dispatcher.
 */
class ExecutorCoroutineDispatcherFactory(private val parallelism: Int) : CoroutineDispatcherFactory {

   // these are the global dispatchers which uses the given threadCount
   private val dispatchers = List(parallelism) { Executors.newSingleThreadExecutor() }
      .map { ExecutorCoroutineDispatcher(it, it.asCoroutineDispatcher()) }

   // these are the dispatchers per spec where the thread count is overriden
   private val dispatchersForSpecs = mutableMapOf<String, ExecutorCoroutineDispatcher>()

   override fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher {
      val dispatcher = dispatchers[abs(spec.bestName().hashCode()) % parallelism].coroutineDispatcher
      log("ExecutorCoroutineDispatcherFactory: Selected dispatcher $dispatcher for ${spec::class}")
      return dispatcher
   }

   override fun dispatcherFor(testCase: TestCase): CoroutineDispatcher {

      // deprecated option - if this test specifies a thread count, then we use dispatchers created solely for this spec
      val resolvedThreadCount = testCase.spec.resolvedThreads() ?: 0
      log("ExecutorCoroutineDispatcherFactory: resolvedThreadCount for ${testCase.spec::class} is $resolvedThreadCount")

      // deprecated option - if this test specifies a thread count, then we use dispatchers created solely for this spec
      if (resolvedThreadCount > 1) {
         return dispatchersForSpecs.getOrPut(testCase.spec::class.bestName()) {
            val executor = Executors.newFixedThreadPool(
               resolvedThreadCount,
               NamedThreadFactory("ExecutorCoroutineDispatcherFactory-%d")
            )
            ExecutorCoroutineDispatcher(executor, executor.asCoroutineDispatcher())
         }.coroutineDispatcher
      }

      // if dispatcher affinity is set we use the same dispatcher as the spec
      return when (testCase.spec.dispatcherAffinity ?: configuration.dispatcherAffinity ?: true) {
         true -> dispatcherFor(testCase.spec::class)
         else -> dispatchers.random().coroutineDispatcher
      }
   }

   override fun stop() {
      dispatchers.forEach { it.executor.shutdown() }
      dispatchersForSpecs.values.forEach { it.executor.shutdown() }
      try {
         dispatchers.forEach { it.executor.awaitTermination(1, TimeUnit.MINUTES) }
         dispatchersForSpecs.values.forEach { it.executor.awaitTermination(1, TimeUnit.MINUTES) }
      } catch (e: InterruptedException) {
         log("ExecutorCoroutineDispatcherFactory: Interrupted while waiting for dispatcher to terminate", e)
         throw e
      }
   }
}
