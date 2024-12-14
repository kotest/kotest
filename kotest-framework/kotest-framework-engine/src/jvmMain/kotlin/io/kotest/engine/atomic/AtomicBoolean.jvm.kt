package io.kotest.engine.atomic

import java.util.concurrent.atomic.AtomicBoolean

actual fun createAtomicBoolean(value: Boolean): io.kotest.engine.atomic.AtomicBoolean {
   return object : io.kotest.engine.atomic.AtomicBoolean {
      private val b = AtomicBoolean(value)
      override fun compareAndSet(expect: Boolean, update: Boolean): Boolean {
         return b.compareAndSet(expect, update)
      }
   }
}
