package io.kotest.common

actual class Synchronizer {
   actual fun<T> synchronized(block: () -> T): T {
      return block()
   }
}
