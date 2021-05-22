package com.sksamuel.kotest.listeners.spec.instanceperleaf

import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

private val counter = AtomicInteger(0)

@AutoScan
class FinalizeSpecTestListener1 : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpecTest::class) {
         counter.incrementAndGet()
      }
   }
}

@AutoScan
class FinalizeSpecTestListener2 : FinalizeSpecListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpecTest::class) {
         counter.incrementAndGet()
      }
   }

   override val name: String = "FinalizeSpecTestListener2"
}

class FinalizeSpecTest : FunSpec() {

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

   init {

      afterProject {
         // both listeners should have fired
         counter.get() shouldBe 8
      }

      // will be added once per instance created
      finalizeSpec {
         counter.incrementAndGet()
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      context("d") {
         test("e").config(enabled = true) {}
      }
   }
}
