package io.kotest.core.specs

import io.kotest.Description
import io.kotest.TestCase
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.fromSpecClass
import io.kotest.core.sourceRef

abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    rootTestCases.add(
      TestCase(
        Description.fromSpecClass(this::class).append(name),
        this,
        { SuiteScope(this).test() },
        sourceRef(),
        TestType.Container,
        TestCaseConfig()
      )
    )
  }

  @KotestDsl
  inner class SuiteScope(val context: TestContext) {
    suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      context.registerTestCase(name, this@SuiteSpec, test, TestCaseConfig(), TestType.Test)
    }
  }
}
