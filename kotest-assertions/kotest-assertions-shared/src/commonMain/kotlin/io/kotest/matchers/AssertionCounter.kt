package io.kotest.matchers

expect val assertionCounter: AssertionCounter

/**
 * An interface for counting assertions in a given context.
 *
 * This is used to track the number of assertions executed, allowing for
 * assertion limits or other behaviors based on the count.
 */
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

   fun getAndReset(): Int {
      val c = get()
      reset()
      return c
   }

   fun inc(count: Int) = repeat(count) { inc() }
}
