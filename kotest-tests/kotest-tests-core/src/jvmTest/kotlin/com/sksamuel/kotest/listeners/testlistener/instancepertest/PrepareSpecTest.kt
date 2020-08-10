package com.sksamuel.kotest.listeners.testlistener.instancepertest

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

@AutoScan
class PrepareSpecTestListener : TestListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == PrepareSpecTest::class) {
         PrepareSpecTest.counter.incrementAndGet()
      }
   }
}

class PrepareSpecTest : FunSpec() {
   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   companion object {
      val counter = AtomicInteger(0)
   }

   init {

      afterProject {
         counter.get() shouldBe 1
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
