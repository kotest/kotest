package io.kotest.core.spec.style.scopes

import io.kotest.core.test.Description
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

object TestDslState {

   private val started = mutableSetOf<String>()
   private val mutex = Semaphore(1)

   suspend fun startTest(name: Description.Test) = mutex.withPermit {
      started.add(name.testPath().value)
   }

   suspend fun clear(name: Description.Test) = mutex.withPermit {
      started.remove(name.testPath().value)
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


