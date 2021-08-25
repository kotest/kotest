package io.kotest.mpp.atomics

import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual class AtomicReference<T> actual constructor(initialValue: T) {

   private val delegate = FreezableAtomicReference(initialValue)

   actual fun get(): T = delegate.value
   actual fun set(value: T) {
      delegate.value = value.freezeIfNeeded()
   }

   actual fun compareAndSet(expected: T, new: T): Boolean =
      delegate.compareAndSet(expected, new.freezeIfNeeded())

   private fun T.freezeIfNeeded(): T {
      if (delegate.isFrozen) {
         freeze()
      }

      return this
   }
}
