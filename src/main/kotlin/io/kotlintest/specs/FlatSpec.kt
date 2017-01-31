package io.kotlintest.specs

import io.kotlintest.*
import java.util.*
import java.util.concurrent.TimeUnit

abstract class FlatSpec : TestBase() {

  companion object {
    data class SpecDef(val name: String, val test: () -> Unit, val annotations: List<Annotation> = emptyList())
  }

  protected val suites: MutableMap<String, TestSuite> = HashMap()

  fun String.config(invocations: Int = 1,
                    ignored: Boolean = false,
                    timeout: Duration = Duration.unlimited,
                    threads: Int = 1,
                    tag: String? = null,
                    tags: List<String> = listOf()): Pair<String, TestConfig> =
      Pair(this, TestConfig(ignored, invocations, timeout, threads, tags))

  @Deprecated(
          message = "use overload instead",
          replaceWith = ReplaceWith("String.config(invocations, ignored, timeout, threads, tag, tags)"))
  fun String.config(invocations: Int = 1,
                    ignored: Boolean = false,
                    timeout: Long = 0,
                    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
                    threads: Int = 1,
                    tag: String? = null,
                    tags: List<String> = listOf()): Pair<String, TestConfig> =
          Pair(this, TestConfig(ignored, invocations, timeout, timeoutUnit, threads, tags))

  // allows us to write "name of test" { test here }
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), test: () -> Unit): SpecDef = SpecDef(this, test, annotations)
  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): SpecDef = this(annotations.toList(), test)

  // combines the suite name and "name of test" with the keyword should
  infix fun Pair<String, TestConfig>.should(pair: SpecDef): Unit {
    val (testName, test, annotations) = pair
    val suite = suites.getOrPut(this.first, {
      val suite = TestSuite.empty(this.first)
      root.nestedSuites.add(suite)
      suite
    })
    suite.cases.add(TestCase(suite, testName, test, defaultTestCaseConfig, annotations))
  }

  infix fun String.should(pair: SpecDef): Unit {
    val suiteName = this
    val (testName, test, annotations) = pair
    val suite = suites.getOrPut(suiteName, {
      val suite = TestSuite.empty(suiteName)
      root.nestedSuites.add(suite)
      suite
    })
    suite.cases.add(TestCase(suite, testName, test, defaultTestCaseConfig, annotations))
  }
}