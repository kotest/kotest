package com.sksamuel.kotlintest.listeners

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeTestTest : FunSpec() {

  private val counter = AtomicInteger(0)

  override fun beforeTest(testCase: TestCase) {
    // should only be invoked for active tests
    counter.incrementAndGet()
  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    counter.get() shouldBe 1
  }

  init {

    test("ignored test").config(enabled = false) {

    }

    test("enabled test").config(enabled = true) {

    }
  }
}