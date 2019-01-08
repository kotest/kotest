package com.sksamuel.kotlintest.listeners

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
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