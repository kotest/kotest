package com.sksamuel.kotest.listeners.spec.singleinstance

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

@ApplyExtension(MyFinalizeSpecListener::class)
class FinalizeSpecApplyExtensionTest : FunSpec() {

   override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

   init {

      afterProject {
         counter.get() shouldBe 1
      }

      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}

class MyFinalizeSpecListener : FinalizeSpecListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      counter.incrementAndGet()
   }
}

private val counter = AtomicInteger(0)
