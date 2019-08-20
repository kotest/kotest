package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestType
import io.kotlintest.core.TestCaseConfig
import io.kotlintest.core.TestContext
import io.kotlintest.extensions.TestCaseExtension

/**
 * Example:
 *
 * "my test" {
 * }
 *
 */
abstract class AbstractStringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun String.config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Long? = null,
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
    addTestCase(this, test, config, TestType.Test)
  }

  operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTestCase(this, test, defaultTestCaseConfig, TestType.Test)
}
