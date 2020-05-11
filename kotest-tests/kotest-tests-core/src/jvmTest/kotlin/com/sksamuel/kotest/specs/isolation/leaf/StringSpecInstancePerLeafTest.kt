package com.sksamuel.kotest.specs.isolation.leaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private var buffer = ""

class StringSpecInstancePerLeafTest : StringSpec({

   afterProject {
      buffer.shouldBe("abc")
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
