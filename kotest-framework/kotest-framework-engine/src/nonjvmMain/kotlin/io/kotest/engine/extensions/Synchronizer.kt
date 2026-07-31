package io.kotest.engine.extensions

internal actual class Synchronizer {
   actual fun<T> synchronized(block: () -> T): T {
      return block()
   }
}
