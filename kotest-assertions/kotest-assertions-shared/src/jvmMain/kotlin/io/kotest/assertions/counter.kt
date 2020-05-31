@file:JvmName("jvmcounter")
package io.kotest.assertions

actual val assertionCounter: AssertionCounter = ThreadLocalAssertionCounter

object ThreadLocalAssertionCounter : AssertionCounter {

   private val context = object : ThreadLocal<Int>() {
      override fun initialValue(): Int = 0
   }

   override fun get(): Int = context.get()
   override fun reset() = context.set(0)
   override fun inc() = context.set(context.get() + 1)
}
