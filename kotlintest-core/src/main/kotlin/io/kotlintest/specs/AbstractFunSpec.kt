package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractFunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  inner class TestBuilder(val name: String) {
    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: suspend TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      addTestCase(name, test, config, TestType.Test)
    }
  }

  fun test(name: String) = TestBuilder(name)

  fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTestCase(name, test, defaultTestCaseConfig, TestType.Test)
}