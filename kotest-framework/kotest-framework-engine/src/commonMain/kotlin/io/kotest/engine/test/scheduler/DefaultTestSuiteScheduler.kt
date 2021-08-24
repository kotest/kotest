package io.kotest.engine.spec

import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.engine.TestSuite
import io.kotest.engine.concurrency.isIsolate
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.reflect.KClass

/**
 * A [TestSuiteScheduler] that schedules specs concurrently, up to a provided [maxConcurrent] value.
 * If the value is 1 then this scheduler will execute specs strictly sequentially.
 *
 * Additionally, on JVM targets, it will recognize the [Isolate] and [DoNotParallelize]
 * annotations to ensure those specs are never scheduled concurrently.
 *
 * @param maxConcurrent The maximum number of concurrent coroutines.
 */
class DefaultTestSuiteScheduler(
   private val maxConcurrent: Int,
) : TestSuiteScheduler {

   override suspend fun schedule(suite: TestSuite, f1: suspend (Spec) -> Unit, f2: suspend (KClass<out Spec>) -> Unit) {
      log { "DefaultTestSuiteScheduler: Launching ${suite.specs.size} specs / ${suite.classes.size} classes" }
      // we partition the classes into those which can run concurrently (default)
      // and those which cannot (see @Isolated)
      // pre-instantiated specs are always treated as isolated
      val (sequential, concurrent) = suite.classes.partition { it.isIsolate() }
      log { "DefaultTestSuiteScheduler: Split specs based on isolation annotations [${sequential.size} sequential ${concurrent.size} concurrent]" }
      schedule(f2, concurrent, maxConcurrent)
      schedule(f2, sequential, 1)
      schedule(f1, suite.specs)
      log { "DefaultSpecLauncher: All specs have completed" }
   }

   private suspend fun schedule(
      f: suspend (KClass<out Spec>) -> Unit,
      classes: List<KClass<out Spec>>,
      concurrency: Int
   ) {
      val semaphore = Semaphore(concurrency)
      // coroutine scope must be outside the loop to ensure that the entire loop can complete before suspending
      coroutineScope {
         classes.forEach { kclass ->
            log { "DefaultTestSuiteScheduler: Scheduling coroutine for spec [$kclass]" }
            launch {
               semaphore.withPermit {
                  log { "DefaultTestSuiteScheduler: Acquired permit for $kclass" }
                  try {
                     f(kclass)
                  } catch (t: Throwable) {
                     log { "DefaultTestSuiteScheduler: Unhandled error during spec execution [$kclass] [$t]" }
                     throw t
                  }
               }
            }
         }
      }
   }

   private suspend fun schedule(f: suspend (Spec) -> Unit, specs: List<Spec>) {
      specs.forEach { spec ->
         // coroutine scope is inside the loop because we want to suspend until the spec is completed
         coroutineScope {
            log { "DefaultTestSuiteScheduler: Scheduling coroutine for spec [$spec]" }
            launch {
               log { "DefaultTestSuiteScheduler: Acquired permit for $spec" }
               try {
                  f(spec)
               } catch (t: Throwable) {
                  log { "DefaultTestSuiteScheduler: Unhandled error during spec execution [$spec] [$t]" }
                  throw t
               }
            }
         }
      }
   }
}
