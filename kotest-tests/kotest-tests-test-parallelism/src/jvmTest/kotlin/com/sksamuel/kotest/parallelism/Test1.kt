package com.sksamuel.kotest.parallelism

import com.sksamuel.kotest.parallelism.ProjectConfig.parallelism
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.sync.Semaphore

class Test1 : FunSpec({
   threads = parallelism

   repeat(parallelism) { k ->
      test("$k") {
         acquire()
         Thread.sleep(100)
         release()
      }
   }
}) {
   companion object {
      var maxLeasesUsed = 0

      val sema = Semaphore(parallelism)

      fun acquire() {
         synchronized(sema) {
            sema.tryAcquire()
            val leasesAcquired = parallelism - sema.availablePermits
            maxLeasesUsed = maxOf(maxLeasesUsed, leasesAcquired)
         }
      }

      fun release() {
         sema.release()
      }
   }
}
