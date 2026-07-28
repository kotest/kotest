package io.kotest.common

expect class Synchronizer() {
   fun<T> synchronized(block: () -> T): T
}
