package com.sksamuel.kotest.listeners

import io.kotest.core.IsolationMode
import io.kotest.SpecClass
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.extensions.TopLevelTest
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeSpecStartedTest : FunSpec() {

  private val counter = AtomicInteger(0)

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  // this should only be invoked once regardless of extra specs instantiated
  override fun beforeSpecClass(spec: SpecClass, tests: List<TopLevelTest>) {
    counter.incrementAndGet()
  }

  override fun afterSpecClass(spec: SpecClass, results: Map<TestCase, TestResult>) {
    counter.get() shouldBe 1
  }

  init {

    test("a") { }
    test("b") { }
    test("c") { }
    test("d") { }
  }
}
