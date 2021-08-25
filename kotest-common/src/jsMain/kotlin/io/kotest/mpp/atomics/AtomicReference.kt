package io.kotest.mpp.atomics

actual class AtomicReference<T> actual constructor(initialValue: T) {

   private var innerValue: T = initialValue

   actual fun get(): T = innerValue
   actual fun set(value: T) {
      innerValue = value
   }

   actual fun compareAndSet(expected: T, new: T): Boolean =
      if (value == expected) {
         value = new
         true
      } else {
         false
      }
}
