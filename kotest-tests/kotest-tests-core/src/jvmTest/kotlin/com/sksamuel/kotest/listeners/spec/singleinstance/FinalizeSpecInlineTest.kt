package com.sksamuel.kotest.listeners.spec.singleinstance

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class FinalizeSpecInlineTest : FunSpec() {

   private val counter = AtomicInteger(0)

   override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

   init {

      // should only run once
      afterProject {
         counter.incrementAndGet()
      }

      afterProject {
         counter.get() shouldBe 1
      }

      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
