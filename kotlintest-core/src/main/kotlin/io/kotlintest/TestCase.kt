package io.kotlintest

import java.time.Duration

/**
 * Describes an actual testcase.
 * That is, a unit of code that will be tested.
 *
 * A testcase is always associated with a container,
 * called a [TestContainer]. Such a descriptor is used
 * to group together related testcases. This allow hierarchical
 * reporting and output.
 */
data class TestCase(
    // the display name is the name of the test that will
    // be outputted in displays. It is most common that
    // this is the name of the test itself but could be
    // derived in other ways.
    val displayName: String,
    // an id unique for all test cases in a single spec
    val id: String,
    // the spec that contains this testcase
    val spec: Spec,
    // the parent test case descriptor
    val descriptor: TestContainer,
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

/**
 * Used to group together [TestCase] instances.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a display name which is used
 * for reporting and display purposes.
 */
data class TestContainer(val name: String) {

  internal val children = mutableListOf<TestContainer>()
  internal val testcases = mutableListOf<TestCase>()

  fun addTest(tc: TestCase) {
    testcases.add(tc)
  }

  fun addContainer(container: TestContainer) {
    children.add(container)
  }

  fun flatten(): List<TestCase> {
    return testcases.toList().plus(children.flatMap { it.flatten() })
  }
}