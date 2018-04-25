package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.lineNumber
import java.time.Duration

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
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: TestContext.() -> Unit): TestCase {
    val config = TestCaseConfig(
        enabled ?: defaultTestCaseConfig.enabled,
        invocations ?: defaultTestCaseConfig.invocations,
        timeout ?: defaultTestCaseConfig.timeout,
        threads ?: defaultTestCaseConfig.threads,
        tags ?: defaultTestCaseConfig.tags,
        extensions ?: defaultTestCaseConfig.extensions)
    val tc = TestCase(root().description().append("should " + this), this@AbstractStringSpec, test, lineNumber(), config)
    addRootScope(tc)
    return tc
  }

  // adds a test directly from the root context
  operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
    val tc = TestCase(rootDescription().append(this), this@AbstractStringSpec, test, lineNumber(), defaultTestCaseConfig)
    addRootScope(tc)
    return tc
  }
}