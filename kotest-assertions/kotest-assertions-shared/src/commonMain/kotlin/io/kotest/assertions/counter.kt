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

object NoopAssertionsCounter : AssertionCounter {
   override fun get(): Int = 1
   override fun reset() {}
   override fun inc() {}
}

fun AssertionCounter.inc(count: Int) = repeat(count) { inc() }

fun AssertionCounter.getAndReset(): Int {
   val c = get()
   reset()
   return c
}
