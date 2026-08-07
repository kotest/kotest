package io.kotest.engine.extensions

internal expect class Synchronizer() {
   fun<T> synchronized(block: () -> T): T
}
