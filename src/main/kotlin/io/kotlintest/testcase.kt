package io.kotlintest

import org.junit.runner.Description
import java.util.concurrent.TimeUnit

data class TestSuite(
    val name: String,
    val nestedSuites: MutableList<TestSuite>,
    val cases: MutableList<TestCase>,
    val annotations: List<Annotation> = emptyList()) {

  companion object {
    fun empty(name: String) = TestSuite(name, mutableListOf<TestSuite>(), mutableListOf<TestCase>())
  }

  internal fun tests(suite: TestSuite = this): List<TestCase> =
      suite.cases + suite.nestedSuites.flatMap { suite -> tests(suite) }

  internal val size = tests().size
}

data class TestConfig(
    val ignored: Boolean = false,
    val invocations: Int = 1,
    val timeout: Duration = Duration.unlimited,
    val threads: Int = 1,
    val tags: List<String> = listOf()) {

  @Deprecated("use the constructor with Duration instead")
  constructor(
      ignored: Boolean,
      invocations: Int,
      timeout: Long = 0,
      timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
      threads: Int,
      tags: List<String>) : this(ignored, invocations, Duration(timeout, timeoutUnit), threads, tags)
}

data class TestCase(
    val suite: TestSuite,
    val name: String,
    val test: () -> Unit,
    var config: TestConfig,
    val annotations: List<Annotation> = emptyList()) {

  val description: Description
    get() = Description.createTestDescription(
        suite.name.replace('.', ' '),
        if (config.invocations < 2) name else name + " (${config.invocations} invocations)", *annotations.toTypedArray())

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
    get() = !config.ignored && isTaggedOrNoTagsSet

  private val isTaggedOrNoTagsSet: Boolean
    get() {
      val systemTags = (System.getProperty("testTags") ?: "").split(',')
      return systemTags.isEmpty() || config.tags.isEmpty() || systemTags.intersect(config.tags).isNotEmpty()
    }
}
