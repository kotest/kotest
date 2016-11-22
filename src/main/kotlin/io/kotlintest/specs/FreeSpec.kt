package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : Spec() {
  init { body() }

  var current = root

  infix operator fun String.minus(init: () -> Unit): Unit {
    val suite = TestSuite.empty(this.replace("(", " ").replace(")", " "))
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(suite = current, name = this.replace("(", " ").replace(")", " "), test = test, config = defaultTestCaseConfig)
    current.cases.add(tc)
    return tc
  }
}