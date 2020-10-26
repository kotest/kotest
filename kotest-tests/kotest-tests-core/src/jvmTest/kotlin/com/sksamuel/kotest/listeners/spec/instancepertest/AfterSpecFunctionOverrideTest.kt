package com.sksamuel.kotest.listeners.spec.instancepertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AfterSpecFunctionOverrideTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   // should be invoked once per isolated test
   override fun afterSpec(spec: Spec) {
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

class AfterSpecFunctionOverrideTestWithNested : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   // should be invoked once per isolated test and per context because it's test itself too
   override fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {

      afterProject {
         counter.get() shouldBe 7
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

