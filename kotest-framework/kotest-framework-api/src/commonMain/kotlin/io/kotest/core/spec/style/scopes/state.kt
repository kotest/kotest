package io.kotest.core.spec.style.scopes

import io.kotest.core.plan.TestPath
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

object TestDslState {

   private val started = mutableSetOf<TestPath>()
   private val mutex = Semaphore(1)

   suspend fun startTest(descriptor: TestPath) = mutex.withPermit {
      started.add(descriptor)
   }

   suspend fun clear(descriptor: TestPath) = mutex.withPermit {
      started.remove(descriptor)
   }

   suspend fun checkState() = mutex.withPermit {
      val unfinished = started.map { "Test was not fully defined: $it" }
      if (unfinished.isNotEmpty())
         error(unfinished.joinToString(", "))
   }

   suspend fun reset() {
      mutex.withPermit {
         started.clear()
      }
   }
}


