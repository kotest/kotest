package io.kotest.assertions

expect object AssertionCounter {

   /**
    * Returns the number of assertions executed in the current context.
    */
   fun get(): Int

   /**
    * Resets the count for the current context
    */
   fun reset()

   /**
    * Increments the counter for the current context.
    */
   fun inc()
}

fun AssertionCounter.getAndReset(): Int {
   val c = get()
   reset()
   return c
}
