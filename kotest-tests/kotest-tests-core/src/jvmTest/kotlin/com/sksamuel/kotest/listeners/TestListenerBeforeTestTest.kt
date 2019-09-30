package com.sksamuel.kotest.listeners

import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
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