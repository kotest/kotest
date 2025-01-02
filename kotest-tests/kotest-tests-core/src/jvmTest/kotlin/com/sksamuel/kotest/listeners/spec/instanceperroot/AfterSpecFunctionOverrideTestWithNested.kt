package com.sksamuel.kotest.listeners.spec.instanceperroot

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AfterSpecFunctionOverrideTestWithNested : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   // should be invoked once per isolated root test
   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {

      afterProject {
         counter.get() shouldBe 2 // not 3 because one of the roots is ignored
      }

      test("ignored test").config(enabled = false) {}

      context("context 1") {
         test("a") { }
         test("b") { }
      }

      context("context 2") {
         test("c") { }
         test("d") { }
      }
   }
}
