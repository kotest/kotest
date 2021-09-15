package io.kotest.core.spec.style.scopes

import io.kotest.core.descriptors.Descriptor
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

object TestDslState {

   private val started = mutableSetOf<String>()
   private val mutex = Semaphore(1)

   suspend fun startTest(name: Descriptor.TestDescriptor) = mutex.withPermit {
      started.add(name.path().value)
   }

   suspend fun clear(name: Descriptor.TestDescriptor) = mutex.withPermit {
      started.remove(name.path().value)
   }

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


