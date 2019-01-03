package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.extensions.TestListener
import io.kotlintest.internal.isActive
import io.kotlintest.specs.AbstractAnnotationSpec
import io.kotlintest.specs.findAfterTestFunctions
import io.kotlintest.specs.findBeforeTestFunctions
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

      executeBefores(listeners)

      fun onComplete(result: TestResult) {
        executeAfters(listeners, result)
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

  private fun executeBefores(listeners: List<TestListener>) {
    if(!testCase.config.enabled) return
    listeners.forEach { it.beforeTest(testCase.description) }
  }

  private fun executeAfters(listeners: List<TestListener>, result: TestResult) {
    if(!testCase.config.enabled) return
    listeners.reversed().forEach { it.afterTest(testCase.description, result) }
  }

  private fun executeTestIfActive(testCase: TestCase, config: TestCaseConfig): TestResult {
    return if (isActive(testCase.copy(config = config))) {
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
