package io.kotest.engine

import io.kotest.core.test.TestCase

interface CoroutineDispatcherAssigner {
   suspend fun assign(testCase: TestCase, f: () -> Unit)
}

/**
 * Each spec has one or more root tests and each of these tests will execute in its own coroutine.
 *
 * By default, a single threaded dispatcher is used and shared for all tests and all specs.
 * This is suitable for tests that suspend but not for tests that block.
 *
 * The parallelism parameter determines how many
 *
 * By default, all tests in the same spec have dispatcher affinity - that is all tests in the same
 * spec will use the same, single threaded, dispatcher, to ensure that callbacks and so forth
 * all operate on the same thread. This avoids memory model issues on the JVM for those who are not
 * familiar with how the JVM guarantees updates to variables are visible across threads.
 *
 * By setting the parallelism argumnent > 1, then
 *
 *  @param parallelism how many threads to use. If > 1 then multiple dispatchers will be created each
 *                    backed by a single thread.
 */
class ExecutorCoroutineDispatcherAssigner(
   private val parallelism: Int,
) : CoroutineDispatcherAssigner {

   override suspend fun assign(testCase: TestCase, f: () -> Unit) {

   }

}

//class DispatcherAffinityCoroutineDispatcherAssigner(
//   parallelism: Int
//) : CoroutineDispatcherAssigner {
//
//   private val dispatcher = Executors.newFixed(parallelism)
//
//   override suspend fun assign(testCase: TestCase, f: () -> Unit) {
//      withContext(dispatcher) {
//         f()
//      }
//   }
//
//}
