package io.kotlintest

import org.junit.runner.Description

data class TestCase(
    val suite: TestSuite,
    val name: String,
    val test: () -> Unit,
    var config: TestCaseConfig,
    val annotations: List<Annotation> = emptyList()) {

  internal val description: Description
    get() = Description.createTestDescription(
        suite.name.replace('.', ' '),
        (if (config.invocations < 2) name else name + " (${config.invocations} invocations)"),
        *(annotations + config.annotations).toSet().toTypedArray()
    )

  /**
   * @param interceptors Interceptors around the test case. Interceptors are processed from left to
   * right.
   */
  fun config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      interceptors: List<(TestCaseContext, () -> Unit) -> Unit>? = null,
      annotations: List<Annotation>? = null) {
    config =
        TestCaseConfig(
            enabled ?: config.enabled,
            invocations ?: config.invocations,
            timeout ?: config.timeout,
            threads ?: config.threads,
            tags ?: config.tags,
            interceptors ?: config.interceptors,
            annotations ?: emptyList())
  }

  internal val isActive: Boolean
    get() = config.enabled && isActiveAccordingToTags

  private val isActiveAccordingToTags: Boolean
    get() {
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

  // required to avoid StackOverflowError due to mutable data structures in this class (suite)
  override fun toString(): String = name
}
