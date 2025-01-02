package com.sksamuel.kotest.parallelism

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
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

@EnabledIf(LinuxCondition::class)
class Spec1 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec2 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec3 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec4 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec5 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec6 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec7 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec8 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec9 : FunSpec({ include(factory(this::class.simpleName!!)) })

@EnabledIf(LinuxCondition::class)
class Spec10 : FunSpec({ include(factory(this::class.simpleName!!)) })

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
