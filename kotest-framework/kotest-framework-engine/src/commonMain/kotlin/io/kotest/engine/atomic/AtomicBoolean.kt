package io.kotest.engine.atomic

interface AtomicBoolean {
   fun compareAndSet(expect: Boolean, update: Boolean): Boolean
}

expect fun createAtomicBoolean(value: Boolean): AtomicBoolean
