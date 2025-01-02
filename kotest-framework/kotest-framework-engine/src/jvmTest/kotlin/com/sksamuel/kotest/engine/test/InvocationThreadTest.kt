package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxCondition::class)
class InvocationThreadTest : FunSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)
//   val multipleThreadMultipleInvocationCallCount = AtomicInteger(0)
//   val multipleThreadMultipleInvocationThreadIds = ConcurrentHashMap<Long, Unit>() // use as concurrent set

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
//      multipleThreadMultipleInvocationCallCount.get() shouldBe 3
//      multipleThreadMultipleInvocationThreadIds.size shouldBeGreaterThan 1
   }

   test("single thread / single invocation").config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   test("single thread / multiple invocations").config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }

//   test("multiple threads / multiple invocations").config(invocations = 3, threads = 3) {
//      multipleThreadMultipleInvocationCallCount.incrementAndGet()
//      multipleThreadMultipleInvocationThreadIds[Thread.currentThread().id] = Unit
//      provokeThreadSwitch()
//   }
})
