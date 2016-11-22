package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
abstract class FunSpec(body: FunSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = name, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}