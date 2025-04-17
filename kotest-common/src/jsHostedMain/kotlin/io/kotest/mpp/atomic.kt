package io.kotest.mpp

actual class AtomicReference<T> actual constructor(initialValue: T) {

   actual var value: T = initialValue

   actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
      if (value == expectedValue) {
         value = newValue
         true
      } else {
         false
      }
}
