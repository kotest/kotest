package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadTest : FunSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   test("single invocation").config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   test("multiple invocations").config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }
})
