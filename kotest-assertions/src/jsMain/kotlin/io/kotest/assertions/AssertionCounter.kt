package io.kotest.assertions

actual object AssertionCounter {

   private var counter = 0

   /**
    * Returns the number of assertions executed in the current context.
    */
   actual fun get(): Int = counter

   /**
    * Resets the count for the current context
    */
   actual fun reset() {
      counter = 0
   }

   /**
    * Increments the counter for the current context.
    */
   actual fun inc() {
      counter++
   }

}
