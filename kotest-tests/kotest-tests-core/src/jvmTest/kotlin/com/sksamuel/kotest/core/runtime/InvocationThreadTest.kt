package com.sksamuel.kotest.core.runtime

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.getOrSet

class InvocationThreadTest : FunSpec({

   val singleThreadSingleInvocationCounter = AtomicInteger(0)
   val singleThreadMultipleInvocationCounter = PersistentThreadLocal<Int>()
   val multipleThreadMultipleInvocationCounter = PersistentThreadLocal<Int>()

   afterSpec {
      singleThreadSingleInvocationCounter.get() shouldBe 1
      singleThreadMultipleInvocationCounter.map.values.sum() shouldBe 5
      multipleThreadMultipleInvocationCounter.map.shouldHaveSize(3)
      multipleThreadMultipleInvocationCounter.map.values.sum() shouldBe 10
   }

   test("single thread / single invocation").config(invocations = 1, threads = 1) {
      singleThreadSingleInvocationCounter.incrementAndGet()
   }

   test("single thread / multiple invocations").config(invocations = 5) {
      val counter = singleThreadMultipleInvocationCounter.getOrSet { 0 }
      singleThreadMultipleInvocationCounter.set(counter + 1)
   }

   test("multiple threads / multiple invocations").config(invocations = 10, threads = 3) {
      val counter = multipleThreadMultipleInvocationCounter.getOrSet { 0 }
      multipleThreadMultipleInvocationCounter.set(counter + 1)
   }
})

class PersistentThreadLocal<T> : ThreadLocal<T>() {

   val map = ConcurrentHashMap<Long, T>()

   override fun set(value: T) {
      super.set(value)
      map[Thread.currentThread().id] = value
   }
}
