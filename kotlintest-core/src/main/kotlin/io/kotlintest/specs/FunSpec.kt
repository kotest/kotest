package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCase =
      rootScope.addTest(name, this@FunSpec, test, defaultTestCaseConfig)
}