package com.sksamuel.kotlintest.listeners

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.fail
import io.kotlintest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerAfterSpecTest : FunSpec() {

  companion object {
    private val counter = AtomicInteger(4)
  }

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  // should be invoked once per isolated test
  override fun afterSpec(spec: Spec) {
    if (counter.decrementAndGet() < 0)
      fail("Error, after spec called too many times")
  }

  init {
    test("a") { }
    test("b") { }
    test("c") { }
    test("d") { }
  }
}