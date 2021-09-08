package io.kotest.engine.concurrency

import io.kotest.core.test.TestCase
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.reflect.KClass

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
class ExecutorCoroutineDispatcherController(
   private val threads: Int, // global threads count
   private val affinity: Boolean,
) : CoroutineDispatcherController {

   // we create single-threaded dispatchers, rather than one dispatcher with multiple threads,
   // so that we can ensure that different tests are allocated the exact same thread if affinity is required
   private val dispatchers = List(threads) { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {

      // if dispatcher affinity is set to true, we use the same dispatcher for the spec and all tests
      // otherwise each test just gets a random dispatcher
      val dispatcher = when (testCase.spec.dispatcherAffinity ?: testCase.spec.dispatcherAffinity() ?: affinity) {
         true -> dispatcherFor(testCase.spec::class)
         else -> dispatchers.random()
      }

      log { "ExecutorCoroutineDispatcherController: Switching context to dispatcher $dispatcher" }
      return withContext(dispatcher) {
         f()
      }
   }

   private fun dispatcherFor(kclass: KClass<*>): CoroutineDispatcher =
      dispatchers[abs(kclass.bestName().hashCode()) % dispatchers.size]

}
