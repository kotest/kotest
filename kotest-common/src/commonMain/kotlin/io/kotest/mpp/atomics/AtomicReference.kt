package io.kotest.mpp.atomics

expect class AtomicReference<T>(initialValue: T) {
   fun get(): T
   fun set(value: T)

   fun compareAndSet(expected: T, new: T): Boolean
}

var <T> AtomicReference<T>.value: T
   get() = get()
   set(value) {
      set(value)
   }
