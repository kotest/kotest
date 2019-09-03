package io.kotlintest.core.specs

import io.kotlintest.Tag
import io.kotlintest.TestType
import io.kotlintest.core.TestCaseConfig
import io.kotlintest.core.TestContext
import io.kotlintest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Example:
 *
 * "my test" {
 * }
 *
 */
abstract class AbstractStringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

  @UseExperimental(ExperimentalTime::class)
  fun String.config(
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
    addTestCase(this, test, config, TestType.Test)
  }

  operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTestCase(this, test, defaultTestCaseConfig, TestType.Test)
}
