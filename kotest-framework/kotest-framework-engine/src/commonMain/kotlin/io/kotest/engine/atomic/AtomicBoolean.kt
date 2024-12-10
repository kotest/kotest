package io.kotest.engine.atomic

interface AtomicBoolean {

   /**
    * Atomically sets the value to [update]
    * if the current value [expect].
    *
    * @return true if successful. False return indicates that
    * the actual value was not equal to the expected value.
    */
   fun compareAndSet(expect: Boolean, update: Boolean): Boolean
}

expect fun createAtomicBoolean(value: Boolean): AtomicBoolean
