package io.kotest.assertions

actual object AssertionCounter {

   private val context = object : ThreadLocal<Int>() {
      override fun initialValue(): Int = 0
   }

   /**
    * Returns the number of assertions executed in the current context.
    */
   actual fun get(): Int = context.get()

   /**
    * Resets the count for the current context
    */
   actual fun reset() = context.set(0)

   /**
    * Increments the counter for the current context.
    */
   actual fun inc() = context.set(context.get() + 1)
}
