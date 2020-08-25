package com.sksamuel.kotest.listeners.spec.singleinstance

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AfterSpecInlineTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

   init {

      afterSpec {
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
