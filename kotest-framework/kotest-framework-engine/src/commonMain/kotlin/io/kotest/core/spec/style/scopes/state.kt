package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

object TestDslState {

   private val started = mutableSetOf<String>()
   private val mutex = Semaphore(1)

   suspend fun startTest(name: TestName) = mutex.withPermit {
      started.add(name.name)
   }

   suspend fun clear(name: TestName) = mutex.withPermit {
      started.remove(name.name)
   }

   /**
    * Will throw an exception if we had a test that was not constructed properly (looks
    * for any test where we invoked .config but did not pass in a test lambda afterwards).
    */
   fun checkState() {
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
