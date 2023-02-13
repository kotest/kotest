package com.sksamuel.kotest.parallelism

import com.sksamuel.kotest.parallelism.ProjectConfig.parallelism
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.sync.Semaphore

fun factory(specName: String) = funSpec {
   test(specName) {
      Leases.acquire()
      Thread.sleep(2000)
      Leases.release()
   }
}

class Spec1 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec2 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec3 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec4 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec5 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec6 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec7 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec8 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec9 : FunSpec({ include(factory(this::class.simpleName!!)) })
class Spec10 : FunSpec({ include(factory(this::class.simpleName!!)) })

object Leases {
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
