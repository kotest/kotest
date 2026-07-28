package io.kotest.common

actual class Synchronizer {
   private val lock = Lock()

   actual fun<T> synchronized(block: () -> T): T {
      synchronized(lock) {
         return block()
      }
   }

   private class Lock
}
