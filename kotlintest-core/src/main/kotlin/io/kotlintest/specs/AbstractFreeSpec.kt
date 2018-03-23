package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) =
      rootScope.addContainer(this, this@AbstractFreeSpec, ::FreeSpecScope, init)

  inner class FreeSpecScope : TestScope() {

    infix operator fun String.minus(init: FreeSpecScope.() -> Unit) =
        addContainer(this, this@AbstractFreeSpec, ::FreeSpecScope, init)

    infix operator fun String.invoke(test: () -> Unit): TestCase =
        addTest(this, this@AbstractFreeSpec, test, defaultTestCaseConfig)
  }
}