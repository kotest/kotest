package com.sksamuel.kotlintest.listeners

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.listener.TopLevelTest
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeSpecStartedTest : FunSpec() {

  private val counter = AtomicInteger(0)

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  // this should only be invoked once regardless of extra specs instantiated
  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    counter.incrementAndGet()
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    counter.get() shouldBe 1
  }

  init {

    test("a") { }
    test("b") { }
    test("c") { }
    test("d") { }
  }
}