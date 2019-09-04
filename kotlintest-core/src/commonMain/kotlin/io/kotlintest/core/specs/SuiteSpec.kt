package io.kotlintest.core.specs

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.core.TestCaseConfig
import io.kotlintest.core.TestContext
import io.kotlintest.core.fromSpecClass
import io.kotlintest.core.sourceRef

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

  @KotlinTestDsl
  inner class SuiteScope(val context: TestContext) {
    suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      context.registerTestCase(name, this@SuiteSpec, test, TestCaseConfig(), TestType.Test)
    }
  }
}
