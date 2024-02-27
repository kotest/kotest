package com.sksamuel.kotest.engine.spec.dsl.aftereach

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class BeforeSpecTest : FunSpec() {

   val listener = object : TestListener {
      override suspend fun beforeSpec(spec: Spec) {
         counter.incrementAndGet()
      }
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   companion object {
      private val counter = AtomicInteger(0)
   }

   init {

      register(listener)

      afterProject {
         counter.get() shouldBe 4
      }

      // this shouldn't trigger the after spec as its in an isolated instance
      test("ignored test").config(enabled = false) {}

      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
