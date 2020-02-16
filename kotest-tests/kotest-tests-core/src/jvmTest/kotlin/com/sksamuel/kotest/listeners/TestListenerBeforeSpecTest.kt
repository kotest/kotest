//package com.sksamuel.kotest.listeners
//
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.spec.SpecConfiguration
//import io.kotest.shouldBe
//import java.util.concurrent.atomic.AtomicInteger
//import kotlin.reflect.KClass
//
//class TestListenerBeforeSpecTest : FunSpec() {
//
//   companion object {
//      private val counter = AtomicInteger(0)
//   }
//
//   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
//
//   // this should be invoked for every spec instance
//   override fun beforeSpec(spec: SpecConfiguration) {
//      counter.incrementAndGet()
//   }
//
//   override fun finalizeSpec(kclass: KClass<out SpecConfiguration>, results: Map<TestCase, TestResult>) {
//      counter.get() shouldBe 4
//   }
//
//   init {
//
//      test("a") { }
//      test("b") { }
//      test("c") { }
//      test("d") { }
//   }
//}
