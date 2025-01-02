package com.sksamuel.kotest.listeners.spec.instanceperroot

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AfterSpecFunctionOverrideTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   // should be invoked once per isolated test
   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {

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

class AfterSpecFunctionOverrideTestWithNested : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   // should be invoked once per isolated test and per context because it's test itself too
   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {

      afterProject {
         counter.get() shouldBe 6
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

