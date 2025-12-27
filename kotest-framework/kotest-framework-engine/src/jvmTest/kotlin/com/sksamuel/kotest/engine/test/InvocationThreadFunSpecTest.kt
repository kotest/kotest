package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadFunSpecTest : FunSpec({

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

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadDescribeSpecTest : DescribeSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   describe("single invocation").config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   describe("multiple invocations").config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }
})


@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadExpectSpecTest : ExpectSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   expect("single invocation").config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   expect("multiple invocations").config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }
})


@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadFreeSpecTest : FreeSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   "single invocation".config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   "multiple invocations".config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }
})

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadBehaviorSpecTest : BehaviorSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   given("context") {
      When("more context") {
         Then("single invocation").config(invocations = 1) {
            singleThreadSingleInvocationCallCount.incrementAndGet()
         }
         Then("single invocation").config(invocations = 5) {
            singleThreadMultipleInvocationCallCount.incrementAndGet()
         }
      }
   }
})

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationThreadShouldSpecTest : ShouldSpec({

   val singleThreadSingleInvocationCallCount = AtomicInteger(0)
   val singleThreadMultipleInvocationCallCount = AtomicInteger(0)

   afterSpec {
      singleThreadSingleInvocationCallCount.get() shouldBe 1
      singleThreadMultipleInvocationCallCount.get() shouldBe 5
   }

   should("single invocation").config(invocations = 1) {
      singleThreadSingleInvocationCallCount.incrementAndGet()
   }

   should("multiple invocations").config(invocations = 5) {
      singleThreadMultipleInvocationCallCount.incrementAndGet()
   }
})
