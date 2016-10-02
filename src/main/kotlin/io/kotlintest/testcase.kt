package io.kotlintest

import org.junit.runner.Description

data class TestCase(
    val suite: TestSuite,
    val name: String,
    val test: () -> Unit,
    var config: TestConfig) {

  val description: Description
    get() = Description.createTestDescription(
        suite.name.replace('.', ' '),
        if (config.invocations < 2) name else name + " (${config.invocations} invocations)")

  /**
   * @param interceptors Interceptors around the test case. Interceptors are processed from left to
   * right.
   */
  fun config(
      invocations: Int? = null,
      ignored: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tag: Tag? = null,
      tags: Set<Tag>? = null,
      interceptors: Iterable<(TestCaseContext, () -> Unit) -> Unit>? = null) {
    config =
        TestConfig(
            ignored ?: config.ignored,
            invocations ?: config.invocations,
            timeout ?: config.timeout,
            threads ?: config.threads,
            tags ?: config.tags,
            tag,
            interceptors ?: config.interceptors)
  }

  val isActive: Boolean
    get() = !config.ignored && isActiveAccordingToTags

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
}
