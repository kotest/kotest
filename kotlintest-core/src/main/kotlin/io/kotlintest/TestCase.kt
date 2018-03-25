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
    // the display name is the name of the test that will
    // be outputted in displays. It is most common that
    // this is the name of the test itself but could be
    // derived in other ways.
    val displayName: String,
    // a slash seperated path to the test case, eg "my test should/do something"
    val path: String,
    // the spec that contains this testcase
    val spec: Spec,
    // a closure of the test itself
    val test: TestContext.() -> Unit,
    // the first line number of the test
    val line: Int,
    // config used when running the test, such as number of
    // invocations, number of threads, etc
    var config: TestCaseConfig) : TestScope {

  override fun name(): String = displayName
  override fun path(): String = path
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

