package com.sksamuel.kotest.listeners

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeSpecTest : FunSpec() {

  companion object {
    private val counter = AtomicInteger(0)
  }

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  // this should be invoked for every spec instance
  override fun beforeSpec(spec: Spec) {
    counter.incrementAndGet()
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    counter.get() shouldBe 4
  }

  init {

    test("a") { }
    test("b") { }
    test("c") { }
    test("d") { }
  }
}