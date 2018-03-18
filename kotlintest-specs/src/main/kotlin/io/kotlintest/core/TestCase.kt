package io.kotlintest.core

import java.time.Duration

/**
 * Describes an actual testcase.
 * That is, a unit of code that will be tested.
 *
 * A testcase is always associated with a context, called
 * a Descriptor. A [TestCaseDescriptor] is used to group together
 * related testcases. This allows hierarchy reporting.
 */
class TestCase(// the display name is the name of the test that will
    // be outputted in displays. It is most common that
    // this is the name of the test itself but could be
    // derived in other ways.
    private val displayName: String,
    // the spec that contains this testcase
    val spec: AbstractSpec,
    // the parent test case descriptor
    val descriptor: TestCaseDescriptor,
    // the function that is the test itself
    val test: () -> Unit,
    // config used when running the test, such as number of
    // invocations, number of threads, etc
    var config: TestCaseConfig) {

  fun config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      interceptors: List<(TestCaseContext, () -> Unit) -> Unit>? = null) {
    config =
        TestCaseConfig(
            enabled ?: config.enabled,
            invocations ?: config.invocations,
            timeout ?: config.timeout,
            threads ?: config.threads,
            tags ?: config.tags,
            interceptors ?: config.interceptors)
  }

  fun isActive() = config.enabled && isActiveAccordingToTags()

  fun isActiveAccordingToTags(): Boolean {
    val testCaseTags = config.tags.map { it.toString() }
    val includedTags = readProperty("includeTags")
    val excludedTags = readProperty("excludeTags")
    val includedTagsEmpty = includedTags.isEmpty() || includedTags == listOf("")
    return when {
      excludedTags.intersect(testCaseTags).isNotEmpty() -> false
      includedTagsEmpty -> true
      includedTags.intersect(testCaseTags).isNotEmpty() -> true
      else -> false
    }
  }

  private fun readProperty(name: String): List<String> =
      (System.getProperty(name) ?: "").split(',').map { it.trim() }
}

/**
 * Used to group together [TestCase] instances.
 * All testcases must have a descriptor parent.
 */
data class TestCaseDescriptor(val name: String) {

  internal val children = mutableListOf<TestCaseDescriptor>()
  internal val testcases = mutableListOf<TestCase>()

  fun addTest(tc: TestCase) {
    testcases.add(tc)
  }

  fun addDescriptor(descriptor: TestCaseDescriptor) {
    children.add(descriptor)
  }
}