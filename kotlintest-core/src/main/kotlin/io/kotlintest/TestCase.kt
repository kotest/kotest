package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

/**
 * Describes an actual testcase.
 * That is, a unit of code that will be tested.
 *
 * A test case is always associated with a container,
 * called a [TestContainer]. Such a descriptor is used
 * to group together related test cases. This allows
 * hierarchical reporting and output using the rich
 * DSL of the [Spec] classes.
 */
data class TestCase(
    // the description contains the names of all parents, plus this one
    val description: Description,
    // the spec that contains this testcase
    val spec: Spec,
    // a closure of the test itself
    val test: TestContext.() -> Unit,
    // the first line number of the test
    val line: Int,
    // config used when running the test, such as number of
    // invocations, number of threads, etc
    var config: TestCaseConfig) : TestScope {

  override fun name(): String = description.name
  override fun description(): Description = description
  override fun spec(): Spec = spec

  fun config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null) {
    config =
        TestCaseConfig(
            enabled ?: config.enabled,
            invocations ?: config.invocations,
            timeout ?: config.timeout,
            threads ?: config.threads,
            tags ?: config.tags,
            extensions ?: config.extensions)
  }

  fun isActive() = config.enabled && isActiveAccordingToTags()

  fun isActiveAccordingToTags(): Boolean {
    val testCaseTags = config.tags.map { it.toString() }
    val includedTags = readTagsProperty("kotlintest.tags.include")
    val excludedTags = readTagsProperty("kotlintest.tags.exclude")
    val includedTagsEmpty = includedTags.isEmpty() || includedTags == listOf("")
    return when {
      excludedTags.intersect(testCaseTags).isNotEmpty() -> false
      includedTagsEmpty -> true
      includedTags.intersect(testCaseTags).isNotEmpty() -> true
      else -> false
    }
  }

  private fun readTagsProperty(name: String): List<String> =
      (System.getProperty(name) ?: "").split(',').map { it.trim() }
}

enum class TestStatus {
  Ignored, Passed, Failed
}

data class TestResult(val status: TestStatus, val error: Throwable?, val metaData: List<Any> = emptyList())