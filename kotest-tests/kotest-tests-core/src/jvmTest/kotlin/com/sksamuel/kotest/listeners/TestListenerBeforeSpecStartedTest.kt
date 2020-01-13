package com.sksamuel.kotest.listeners

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeSpecStartedTest : FunSpec() {

   private val counter = AtomicInteger(0)

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   init {

      // this should only be invoked once regardless of extra specs instantiated
      prepareSpec {
         counter.incrementAndGet()
      }

      finalizeSpec {
         counter.get() shouldBe 1
      }

      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
