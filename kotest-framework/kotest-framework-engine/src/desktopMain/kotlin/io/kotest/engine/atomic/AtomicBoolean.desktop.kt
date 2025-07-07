package io.kotest.engine.atomic

import kotlin.concurrent.AtomicInt

actual fun createAtomicBoolean(value: Boolean): AtomicBoolean {
   return object : AtomicBoolean {
      private var atomic = AtomicInt(if (value) 1 else 0)
      override fun compareAndSet(expect: Boolean, set: Boolean): Boolean {
         if (expect == value) {
            atomic.compareAndSet(if (expect) 1 else 0, if (set) 1 else 0)
            return true
         } else {
            return false
         }
      }

      override fun get(): Boolean {
         return atomic.value == 1
      }
   }
}
