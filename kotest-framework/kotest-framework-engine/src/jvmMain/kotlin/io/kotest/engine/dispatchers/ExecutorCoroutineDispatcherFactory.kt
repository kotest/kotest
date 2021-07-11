package io.kotest.engine.dispatchers

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.concurrency.resolvedThreads
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

@ExperimentalKotest
data class ExecutorCoroutineDispatcher(
   val executor: ExecutorService,
   val coroutineDispatcher: CoroutineDispatcher,
)

/**
 * A [CoroutineDispatcherFactory] that will create single threaded dispatchers.
 *
 * @param parallelism how many threads to use. If > 1 then multiple dispatchers will be created each
 *                    backed by a single thread).
 *
 * @param dispatcherAffinity if true then this factory guarantees dispatcher affinity - that is a particular
 *                           spec and all the tests in it will receive the same dispatcher.
 *                           This value is overriden if specified in the spec itself.
 */
@ExperimentalKotest
class ExecutorCoroutineDispatcherFactory(
   private val parallelism: Int,
   private val dispatcherAffinity: Boolean
) : CoroutineDispatcherFactory {

   // these are the global dispatchers which uses the given threadCount
   private val dispatchers = List(parallelism) { Executors.newSingleThreadExecutor() }
      .map { ExecutorCoroutineDispatcher(it, it.asCoroutineDispatcher()) }

   // these are the dispatchers per spec where the thread count is overriden
   private val dispatchersForSpecs = mutableMapOf<String, ExecutorCoroutineDispatcher>()

   override fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher {
      val dispatcher = dispatchers[abs(spec.bestName().hashCode()) % parallelism].coroutineDispatcher
      log { "ExecutorCoroutineDispatcherFactory: Selected dispatcher $dispatcher for ${spec.bestName()}" }
      return dispatcher
   }

   override fun dispatcherFor(testCase: TestCase): CoroutineDispatcher {

      // deprecated option - if this test specifies a thread count, then we use dispatchers created solely for this spec
      val resolvedThreadCount = testCase.spec.resolvedThreads() ?: 0
      log { "ExecutorCoroutineDispatcherFactory: resolvedThreadCount for ${testCase.spec::class} is $resolvedThreadCount" }

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
      return when (testCase.spec.dispatcherAffinity ?: testCase.spec.dispatcherAffinity() ?: dispatcherAffinity) {
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
         log(e) { "ExecutorCoroutineDispatcherFactory: Interrupted while waiting for dispatcher to terminate" }
         throw e
      }
   }
}
