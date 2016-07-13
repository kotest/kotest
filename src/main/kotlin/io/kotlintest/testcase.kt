package io.kotlintest

import org.junit.runner.Description
import java.util.concurrent.TimeUnit

data class TestCase(
    val suite: TestSuite,
    val name: String,
    val test: () -> Unit,
    var config: TestConfig) {

  val description: Description
    get() = Description.createTestDescription(
        suite.name.replace('.', ' '),
        if (config.invocations < 2) name else name + " (${config.invocations} invocations)")

  fun config(
      invocations: Int = 1,
      ignored: Boolean = false,
      timeout: Duration = Duration.unlimited,
      threads: Int = 1,
      tag: String? = null,
      tags: List<String> = listOf()): Unit {
    val mergedTags = if (tag != null) tags + tag else config.tags
    config = config.copy(ignored, invocations, timeout, threads, mergedTags)
  }

  @Deprecated(
      message = "use overload instead",
      replaceWith = ReplaceWith("config(invocations, ignored, timeout, threads, tag, tags)"))
  fun config(
      invocations: Int = 1,
      ignored: Boolean = false,
      timeout: Long = 0,
      timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
      threads: Int = 1,
      tag: String? = null,
      tags: List<String> = listOf()): Unit {
    val mergedTags = if (tag != null) tags + tag else config.tags
    config = config.copy(ignored, invocations, Duration(timeout, timeoutUnit), threads, mergedTags)
  }

  internal val isActive: Boolean
    get() = !config.ignored && isActiveAccordingToTags

  private val isActiveAccordingToTags: Boolean
    get() {
      val includedTags = readProperty("includeTags")
      val excludedTags = readProperty("excludeTags")
      val includedTagsEmpty = includedTags.isEmpty() || includedTags == listOf("")
      return when {
        excludedTags.intersect(config.tags).isNotEmpty() -> false
        includedTagsEmpty -> true
        includedTags.intersect(config.tags).isNotEmpty() -> true
        else -> false
      }
    }

  private fun readProperty(name: String): List<String> =
      (System.getProperty(name) ?: "").split(',').map { it.trim() }
}
