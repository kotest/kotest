package io.kotest.engine.atomic

actual fun createAtomicBoolean(value: Boolean): AtomicBoolean {
   return object : AtomicBoolean {
      private var b = false
      override fun compareAndSet(expect: Boolean, update: Boolean): Boolean {
         if (expect == value) {
            b = update
            return true
         } else {
            return false
         }
      }
   }
}
