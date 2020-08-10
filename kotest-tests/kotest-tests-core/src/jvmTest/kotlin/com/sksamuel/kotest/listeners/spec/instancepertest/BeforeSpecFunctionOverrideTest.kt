package com.sksamuel.kotest.listeners.spec.instancepertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests beforeSpec inside a spec when overriding at the method level.
 */
class BeforeSpecFunctionOverrideTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   override fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {

      afterProject {
         counter.get() shouldBe 5
      }

      test("ignored test").config(enabled = false) {}
      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
