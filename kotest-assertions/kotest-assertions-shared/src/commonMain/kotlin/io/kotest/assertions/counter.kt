package io.kotest.assertions

interface AssertionCounter {

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

// the single assertion counter instance to be used by all clients
expect val assertionCounter: AssertionCounter

open class BasicAssertionCounter : AssertionCounter {
   private var counter = 0
   override fun get(): Int = counter
   override fun reset() {
      counter = 0
   }

   override fun inc() {
      counter++
   }
}

fun AssertionCounter.getAndReset(): Int {
   val c = get()
   reset()
   return c
}
