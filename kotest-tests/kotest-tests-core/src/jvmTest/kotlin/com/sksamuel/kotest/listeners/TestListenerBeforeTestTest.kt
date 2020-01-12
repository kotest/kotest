package com.sksamuel.kotest.listeners

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerBeforeTestTest : FunSpec() {

  private val counter = AtomicInteger(0)

  override suspend fun beforeTest(testCase: TestCase) {
    // should only be invoked for active tests
    counter.incrementAndGet()
  }

  override suspend fun afterTest(testCase: TestCase, result: TestResult) {
    counter.get() shouldBe 1
  }

  init {

    test("ignored test").config(enabled = false) {

    }

    test("enabled test").config(enabled = true) {

    }
  }
}
