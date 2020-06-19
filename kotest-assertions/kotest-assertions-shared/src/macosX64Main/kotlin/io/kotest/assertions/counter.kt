package io.kotest.assertions

actual val assertionCounter: AssertionCounter = NativeAssertionCounter

@ThreadLocal
object NativeAssertionCounter : AssertionCounter {
   private var counter = 0
   override fun get(): Int = counter
   override fun reset() {
      counter = 0
   }

   override fun inc() {
      counter++
   }
}
