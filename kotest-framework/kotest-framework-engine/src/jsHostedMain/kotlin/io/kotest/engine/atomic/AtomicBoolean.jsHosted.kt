package io.kotest.engine.atomic

actual fun createAtomicBoolean(value: Boolean): AtomicBoolean {
   return object : AtomicBoolean {
      private var b = false
      override fun compareAndSet(expect: Boolean, set: Boolean): Boolean {
         if (expect == value) {
            b = set
            return true
         } else {
            return false
         }
      }

      override fun get(): Boolean {
         return b
      }
   }
}
