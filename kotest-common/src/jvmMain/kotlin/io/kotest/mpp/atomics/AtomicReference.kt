package io.kotest.mpp.atomics

actual class AtomicReference<T> actual constructor(initialValue: T) {

   private val delegate = java.util.concurrent.atomic.AtomicReference(value)
   actual fun get() = delegate.get()
   actual fun set(value: T) {
         delegate.set(value)
      }

   actual fun compareAndSet(expected: T, new: T): Boolean = delegate.compareAndSet(expected, new)
}
