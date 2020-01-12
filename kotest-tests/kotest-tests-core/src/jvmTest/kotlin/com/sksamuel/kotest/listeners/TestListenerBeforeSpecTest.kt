package com.sksamuel.kotest.listeners

import io.kotest.core.IsolationMode
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.SpecConfiguration
import io.kotest.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeSpecTest : FunSpec() {

   companion object {
      private val counter = AtomicInteger(0)
   }

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   // this should be invoked for every spec instance
   override fun beforeSpec(spec: SpecConfiguration) {
      counter.incrementAndGet()
   }

   override fun finalizeSpec(spec: SpecConfiguration, results: Map<TestCase, TestResult>) {
      counter.get() shouldBe 4
   }

   init {

      test("a") { }
      test("b") { }
      test("c") { }
      test("d") { }
   }
}
