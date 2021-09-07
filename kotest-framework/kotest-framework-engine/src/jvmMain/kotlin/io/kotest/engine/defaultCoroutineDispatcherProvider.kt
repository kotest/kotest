package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.mpp.bestName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.reflect.KClass

actual val defaultCoroutineDispatcherProvider: CoroutineDispatcherAssigner =
   ExecutorCoroutineDispatcherAssigner(configuration.parallelism, configuration.dispatcherAffinity)

/**
 * Each spec has one or more root tests and each of these tests will execute in its own coroutine.
 *
 * By default, a single threaded dispatcher is used and shared for all tests and all specs.
 * This is suitable for tests that suspend but not for tests that block.
 *
 * The [threads] parameter in global configuration allows overriding how many threads
 * are used when executing tests.
 *
 * By default, all tests in the same spec have dispatcher [affinity] - that is all tests in the same
 * spec will always use the same thread, to ensure that all callbacks for all tests (e.g. beforeTest)
 * operate on the same thread. This helps avoid subtle memory model issues on the JVM for those who are not
 * familiar with how the JVM guarantees updates to variables are visible across threads.
 *
 * This does mean however that inside a given spec, a blocked test will also block other tests in
 * that spec. Swings and roundabouts. To allow each test to have its own thread, set dispatcher
 * affinity to false, either globally, or on a per spec basis.
 */
class ExecutorCoroutineDispatcherAssigner(
   private val threads: Int, // global threads count
   private val affinity: Boolean,
) : CoroutineDispatcherAssigner {

   // these are the global dispatchers which uses the given threadCount
   private val dispatchers = List(threads) { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {

      // if dispatcher affinity is set to true, we pick a dispatcher for the spec and stick with it
      // otherwise each test just gets a random dispatcher
      val dispatcher = when (testCase.spec.dispatcherAffinity ?: testCase.spec.dispatcherAffinity() ?: affinity) {
         true -> dispatcherFor(testCase.spec::class)
         else -> dispatchers.random()
      }

      return withContext(dispatcher) {
         f()
      }
   }

   private fun dispatcherFor(kClass: KClass<*>): CoroutineDispatcher =
      dispatchers[abs(kClass.bestName().hashCode()) % dispatchers.size]

}
