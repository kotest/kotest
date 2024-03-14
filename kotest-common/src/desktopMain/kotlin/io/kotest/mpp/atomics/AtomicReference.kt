package io.kotest.mpp.atomics

actual class AtomicReference<T> actual constructor(initialValue: T) {

   private val delegate = kotlin.concurrent.AtomicReference(initialValue)

   actual var value: T
      get() = delegate.value
      set(value) {
         delegate.value = value.freezeIfNeeded()
      }

   actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
      delegate.compareAndSet(expectedValue, newValue.freezeIfNeeded())

   private fun T.freezeIfNeeded(): T {
      return this
   }
}
