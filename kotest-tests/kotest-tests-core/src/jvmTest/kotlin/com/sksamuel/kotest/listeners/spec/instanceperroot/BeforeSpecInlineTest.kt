package com.sksamuel.kotest.listeners.spec.instanceperroot

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests beforeSpec inside a spec using the dsl.
 */
class BeforeSpecInlineTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   init {

      beforeSpec {
         counter.incrementAndGet()
      }

      afterProject {
         counter.get() shouldBe 4
      }

      // this shouldn't trigger the after spec as its in an isolated instance
      test("ignored test").config(enabled = false) {}

      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
