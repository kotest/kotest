package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.core.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

/**
 * A [CoroutineDispatcherFactory] that uses a fixed number of threads that are shared between
 * all specs that use this factory.
 *
 * If [affinity] is true, then the same thread will be assigned to a spec and all it's tests.
 * This ensures that all tests and callbacks in a single spec are using the same thread.
 * This option can be overridden at the spec level.
 *
 * Affinity helps avoid subtle memory model issues on the JVM for those who are not
 * familiar with how the JVM guarantees updates to variables are visible across threads.
 *
 * This does mean however that inside a given spec, a blocked test will also block other tests in
 * that spec. Each test can be set to use its own thread by setting the test config `blockedTest` to true.
 *
 * As factories can be shared across specs, it is possible to create an instance of this factory
 * and assign it to be used by several specs independently of others.
 */
class FixedThreadCoroutineDispatcherFactory(
   threads: Int,
   private val affinity: Boolean,
) : CoroutineDispatcherFactory, Closeable {

   private val logger = Logger(FixedThreadCoroutineDispatcherFactory::class)

   private val dispatcherPool = List(threads) { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

   private val requestCount = AtomicInteger(0)

   private val pinnedDispatchers = ConcurrentHashMap<KClass<*>, CoroutineDispatcher>()

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {

      val resolvedAffinity = testCase.spec.dispatcherAffinity ?: testCase.spec.dispatcherAffinity() ?: affinity
      logger.log { Pair(testCase.name.testName, "affinity=$resolvedAffinity") }

      // if dispatcher affinity is set to true, we pick a dispatcher for the spec and stick with it
      // otherwise each test just gets a dispatcher from the pool in a round-robin fashion
      val dispatcher = when (resolvedAffinity) {
         true -> pinnedDispatchers.getOrPut(testCase.spec::class, ::nextDispatcher)
         else -> nextDispatcher()
      }

      logger.log { Pair(testCase.name.testName, "Switching dispatcher to $dispatcher") }
      return withContext(dispatcher) {
         f()
      }
   }

   /**
    * Close dispatchers created by the factory, releasing threads.
    */
   override fun close() {
      dispatcherPool.forEach { it.close() }
   }

   /**
    * Returns the next dispatcher from the pool in a round-robin fashion.
    */
   private fun nextDispatcher() = dispatcherPool[requestCount.getAndIncrement() % dispatcherPool.size]
}
