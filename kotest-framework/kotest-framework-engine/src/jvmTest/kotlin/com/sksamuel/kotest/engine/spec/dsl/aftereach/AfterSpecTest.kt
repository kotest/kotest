package com.sksamuel.kotest.engine.spec.dsl.aftereach

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AfterSpecTest : FunSpec() {

   val listener = object : TestListener {
      override suspend fun afterSpec(spec: Spec) {
         counter.incrementAndGet()
      }
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   companion object {
      private val counter = AtomicInteger(0)
   }

   init {

      extension(listener)

      afterProject {
         counter.get() shouldBe 4
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
