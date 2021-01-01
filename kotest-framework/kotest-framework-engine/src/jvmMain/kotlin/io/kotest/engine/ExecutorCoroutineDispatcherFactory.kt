package io.kotest.engine

import io.kotest.core.extensions.CoroutineDispatcherFactoryExtension
import io.kotest.core.internal.resolvedDispatcher
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
import kotlin.reflect.KClass

data class ExecutorBackedCoroutineDispatcher(
   val executor: ExecutorService,
   val coroutineDispatcher: CoroutineDispatcher,
)

/**
 * A default implementation of [CoroutineDispatcherFactoryExtension] that creates coroutine dispatchers
 * backed by JVM executors. Each spec and all the root tests in that spec will receive the same single-threaded
 * dispatcher unless that spec sets the number of threads > 1.
 */
class ExecutorCoroutineDispatcherFactory(private val threadCount: Int) : CoroutineDispatcherFactoryExtension {

   // these are the global dispatchers which uses the given threadCount
   private val globalDispatchers = List(threadCount) { Executors.newSingleThreadExecutor() }
      .map { ExecutorBackedCoroutineDispatcher(it, it.asCoroutineDispatcher()) }

   // these are the dispatchers per spec where the thread count is overriden
   private val dispatchersForSpecs = mutableMapOf<String, ExecutorBackedCoroutineDispatcher>()

   override fun dispatcherFor(spec: KClass<out Spec>): CoroutineDispatcher {
      return globalDispatchers[spec.bestName().hashCode() % threadCount].coroutineDispatcher
   }

   override fun dispatcherFor(testCase: TestCase): CoroutineDispatcher {

      // if we have a dispatcher explicitly set on the spec, we always use that
      val dispatcher = testCase.spec.resolvedDispatcher()
      log("ExecutorCoroutineDispatcherFactory: resolvedDispatcher for ${testCase.spec::class} is $dispatcher")
      if (dispatcher != null) return dispatcher

      val resolvedThreadCount = testCase.spec.resolvedThreads() ?: 0
      log("ExecutorCoroutineDispatcherFactory: resolvedThreadCount for ${testCase.spec::class} is $resolvedThreadCount")

      // if this test specifies a thread count, then we use dispatchers created solely for this spec
      if (resolvedThreadCount > 1) {
         return dispatchersForSpecs.getOrPut(testCase.spec::class.bestName()) {
            val executor = Executors.newFixedThreadPool(resolvedThreadCount, NamedThreadFactory("ExecutorCoroutineDispatcherFactory-%d"))
            ExecutorBackedCoroutineDispatcher(executor, executor.asCoroutineDispatcher())
         }.coroutineDispatcher
      }

      // the default is to use the global dispatchers
      return globalDispatchers[testCase.spec::class.bestName().hashCode() % threadCount].coroutineDispatcher
   }

   override fun stop() {
      globalDispatchers.forEach { it.executor.shutdown() }
      dispatchersForSpecs.values.forEach { it.executor.shutdown() }
      try {
         globalDispatchers.forEach { it.executor.awaitTermination(1, TimeUnit.MINUTES) }
         dispatchersForSpecs.values.forEach { it.executor.awaitTermination(1, TimeUnit.MINUTES) }
      } catch (e: InterruptedException) {
         log("ExecutorCoroutineDispatcherFactory: Interrupted while waiting for dispatcher to terminate", e)
         throw e
      }
   }
}
