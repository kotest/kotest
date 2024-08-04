package com.sksamuel.kotest.engine.spec.dsl.aftereach

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

internal class AfterSpecInheritenceTest : ParentTest() {

   init {

      afterProject {
         counter.get() shouldBe 0
      }

      test("after spec on parent should be invoked").config(enabled = true) {}
   }
}

internal abstract class ParentTest : FunSpec() {

   companion object {
      val counter = AtomicInteger(0)
   }

   init {

      afterSpec {
         counter.incrementAndGet()
      }
   }
}
