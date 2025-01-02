package com.sksamuel.kotest.engine.spec.isolation.perleaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private var buffer = ""

class FunSpecInstancePerLeafTest : FunSpec({

   afterProject {
      buffer.shouldBe("abc")
   }

   isolationMode = IsolationMode.InstancePerLeaf

   val counter = AtomicInteger(0)

   test("a") {
      buffer += "a"
      counter.incrementAndGet() shouldBe 1
   }

   test("b") {
      buffer += "b"
      counter.incrementAndGet() shouldBe 1
   }

   test("c") {
      buffer += "c"
      counter.incrementAndGet() shouldBe 1
   }
})
