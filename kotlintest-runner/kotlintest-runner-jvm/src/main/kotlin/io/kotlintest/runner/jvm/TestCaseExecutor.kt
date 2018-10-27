package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestFilterResult
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import org.slf4j.LoggerFactory
import java.time.Duration

class TestCaseExecutor(val listener: TestEngineListener,
                       val testCase: TestCase,
                       val context: TestContext) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  fun execute() {
    try {

      listener.prepareTestCase(testCase)

      val listeners = listOf(testCase.spec) + testCase.spec.listeners() + Project.listeners()

      val extensions = testCase.config.extensions +
          testCase.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      listeners.forEach { it.beforeTest(testCase.description) }

      fun onComplete(result: TestResult) {
        listeners.reversed().forEach { it.afterTest(testCase.description, result) }
        listener.completeTestCase(testCase, result)
      }

      fun interceptTestCase(remaining: List<TestCaseExtension>,
                            config: TestCaseConfig,
                            onComplete: (TestResult) -> Unit) {
        when {
          remaining.isEmpty() -> {
            val result = executeTestIfActive(testCase, config)
            onComplete(result)
          }
          else -> {
            val ctx = TestCaseInterceptContext(testCase.description, testCase.spec, config)
            remaining.first().intercept(ctx, { conf, callback -> interceptTestCase(remaining.drop(1), conf, callback) }, { onComplete(it) })
          }
        }
      }

      interceptTestCase(extensions, testCase.config, ::onComplete)

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.completeTestCase(testCase, TestResult.error(t))
    }
  }

  private fun executeTestIfActive(testCase: TestCase, config: TestCaseConfig): TestResult {

    val bang = testCase.name.startsWith("!") && System.getProperty("kotlintest.bang.disable") == null
    val tags = config.tags + testCase.spec.tags()
    val excluded = Project.testCaseFilters().map { it.filter(testCase.description) }.any { it == TestFilterResult.Ignore }
    val enabled = config.enabled && Project.tags().isActive(tags) && !bang && !excluded

    return if (enabled) {
      TestSetExecutor(listener, context).execute(TestSet(testCase, config.timeout, config.invocations, config.threads))
    } else {
      TestResult.Ignored
    }
  }
}

/**
 * A testset comprises the parameters of a particular [TestCase]s execution.
 */
data class TestSet(val testCase: TestCase,
                   val timeout: Duration,
                   val invocations: Int,
                   val threads: Int)
