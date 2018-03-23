package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase

abstract class AbstractFunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCase =
      rootScope.addTest(name, this@AbstractFunSpec, test, defaultTestCaseConfig)
}