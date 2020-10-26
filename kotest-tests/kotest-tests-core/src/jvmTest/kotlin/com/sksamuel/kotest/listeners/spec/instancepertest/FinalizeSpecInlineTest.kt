package com.sksamuel.kotest.listeners.spec.instancepertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class FinalizeSpecInlineTest : FunSpec() {

   private val counter = AtomicInteger(0)

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   init {

      // should only run once
      finalizeSpec {
         counter.incrementAndGet()
      }

      afterProject {
         counter.get() shouldBe 1
      }

      test("ignored test").config(enabled = false) {}
      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
