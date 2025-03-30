package com.sksamuel.kotest.parallelism

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.TestExecutionMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlin.time.Duration.Companion.seconds

val linux = System.getProperty("os.name").lowercase().contains("linux")

@EnabledIf(NotMacOnGithubCondition::class)
class ParallelTests : FunSpec() {
   init {

      // allow all tests to be dispatched at once
      testExecutionMode = TestExecutionMode.Concurrent

      for (k in 1..10) {
         test("test$k") {
            Leases.acquire()
            delay(1.seconds) // this will cause another lease to be acquired by the next test
            Leases.release()
         }
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      // There are 10 specs, and each one has a test with a delay of 1s.
      // If parallel execution is working, all specs should run at the same time
      //
      // We allow a large margin of error here as the GitHub runners seem to have a lot of contention.
      if (linux)
         if (Leases.maxLeasesUsed < 10) {
            error(
               "Parallel execution failure: max leases used was ${Leases.maxLeasesUsed} but should have been 10."
            )
         }
   }
}

object Leases {
   var maxLeasesUsed = 0

   val semaphore = Semaphore(10)

   fun acquire() {
      synchronized(semaphore) {
         semaphore.tryAcquire()
         val leasesAcquired = 10 - semaphore.availablePermits
         maxLeasesUsed = maxOf(maxLeasesUsed, leasesAcquired)
      }
   }

   fun release() {
      semaphore.release()
   }
}
