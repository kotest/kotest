package io.kotest.mpp.atomics

expect class AtomicReference<T>(initialValue: T) {
   var value: T
   fun compareAndSet(expectedValue: T, newValue: T): Boolean
}
