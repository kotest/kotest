package com.sksamuel.kotest.engine.spec.isolation.perleaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private var buffer = ""

class StringSpecInstancePerLeafTest : StringSpec({

   beforeSpec {
      buffer += "-init-"
   }

   afterSpec {
      buffer.shouldBe("-init-a-init-b-init-c")
   }

   isolationMode = IsolationMode.InstancePerLeaf

   val counter = AtomicInteger(0)

   "a" {
      buffer += "a"
      counter.incrementAndGet() shouldBe 1
   }

   "b" {
      buffer += "b"
      counter.incrementAndGet() shouldBe 1
   }

   "c" {
      buffer += "c"
      counter.incrementAndGet() shouldBe 1
   }
})
