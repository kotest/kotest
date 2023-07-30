@file:JvmName("jvmcounter")
package io.kotest.assertions

actual val assertionCounter: AssertionCounter = ThreadLocalAssertionCounter

object ThreadLocalAssertionCounter : AssertionCounter {

   val values = object : ThreadLocal<Int>() {
      override fun initialValue(): Int = 0
   }

   override fun get(): Int = values.get()
   override fun reset() = values.set(0)
   override fun inc() = values.set(values.get() + 1)
}
