package io.kotest.engine.concurrency

import io.kotest.common.concurrentHashMap
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.mpp.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

/**
 * A [CoroutineDispatcherFactory] that uses a fixed number of threads that are shared between
 * all specs that use this factory.
 *
 * If [affinity] is true, then the same thread will be assigned to a spec and all it's tests.
 * This ensures that all tests and callbacks in a single spec are using the same thread.
 * This option can be overriden at the spec level.
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
) : CoroutineDispatcherFactory {

   private val logger = Logger(FixedThreadCoroutineDispatcherFactory::class)
   private val dispatchers = List(threads) { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }
   private val cursor = AtomicInteger(0)

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {

      val resolvedAffinity = testCase.spec.dispatcherAffinity ?: testCase.spec.dispatcherAffinity() ?: affinity
      logger.log { Pair(testCase.name.testName, "affinity=$resolvedAffinity") }

      // if dispatcher affinity is set to true, we pick a dispatcher for the spec and stick with it
      // otherwise each test just gets a random dispatcher
      val dispatcher = when (resolvedAffinity) {
         true -> dispatcherFor(testCase.spec::class)
         else -> dispatchers[cursor.incrementAndGet() % dispatchers.size]
      }

      logger.log { Pair(testCase.name.testName, "Switching dispatcher to $dispatcher") }
      return withContext(dispatcher) {
         f()
      }
   }


   /**
    * Returns a consistent dispatcher for the given [kclass].
    */
   private fun dispatcherFor(kclass: KClass<*>): CoroutineDispatcher =
      dispatcherAffinity[kclass] ?: dispatchers[cursor.getAndIncrement() % dispatchers.size].also {
         dispatcherAffinity[kclass] = it
      }

   private val dispatcherAffinity = concurrentHashMap<KClass<*>, CoroutineDispatcher>()
}
