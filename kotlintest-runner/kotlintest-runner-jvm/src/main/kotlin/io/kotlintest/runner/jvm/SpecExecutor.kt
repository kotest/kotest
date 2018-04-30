package io.kotlintest.runner.jvm

import createSpecInterceptorChain
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import io.kotlintest.TestStatus
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class SpecExecutor(val listener: TestEngineListener) {

  abstract fun execute(spec: Spec)

  protected fun interceptSpec(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach { it.beforeSpec(spec.description(), spec) }

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.description(), spec)
    val chain = createSpecInterceptorChain(context, extensions) {
      afterInterception()
    }
    chain.invoke()

    listeners.reversed().forEach { it.afterSpec(spec.description(), spec) }
  }

  protected fun executeTestCase(scope: TestScope, context: TestContext) {
    try {

      listener.executionStarted(scope)
      val listeners = listOf(scope.spec) + scope.spec.listeners() + Project.listeners()

      val extensions = scope.config.extensions +
          scope.spec.extensions().filterIsInstance<TestCaseExtension>() +
          Project.testCaseExtensions()

      listeners.forEach { it.beforeTest(scope.description) }

      interceptTestScope(scope, extensions, scope.config, { config ->
        if (config.enabled && Project.tags().isActive(config.tags)) {
          testCaseInvocations(scope, config, context)
        } else {
          TestResult.Ignored
        }
      }, { result ->
        listeners.reversed().forEach { it.afterTest(scope.description, result) }
        listener.executionFinished(scope, result)
      })

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.executionFinished(scope, TestResult.error(t))
    }
  }

  protected fun interceptTestScope(testScope: TestScope,
                                   extensions: List<TestCaseExtension>,
                                   config: TestCaseConfig,
                                   afterInterception: (TestCaseConfig) -> TestResult,
                                   onComplete: (TestResult) -> Unit) {

    when {
      extensions.isEmpty() -> {
        val result = afterInterception(config)
        onComplete(result)
      }
      else -> {
        val context = TestCaseInterceptContext(testScope.description, testScope.spec, config)
        extensions.first().intercept(context, { conf, callback -> interceptTestScope(testScope, extensions.drop(1), conf, afterInterception, callback) }, { onComplete(it) })
      }
    }
  }

  protected fun testCaseInvocations(scope: TestScope, config: TestCaseConfig, context: TestContext): TestResult {
    // each test runs inside its own execution service, so we can easily support multiple threads
    val executor = Executors.newFixedThreadPool(config.threads)
    val metadata = ConcurrentHashMap<String, Any?>()
    val errors = mutableListOf<Throwable>()

    for (j in 1..config.invocations) {
      executor.execute {
        try {
          scope.test(context)
          val error = context.error()
          if (error != null)
            errors.add(error)
        } catch (t: Throwable) {
          errors.add(t)
        } finally {
          metadata.putAll(context.metaData())
        }
      }
    }

    executor.shutdown()
    val terminated = executor.awaitTermination(config.timeout.seconds, TimeUnit.SECONDS)
    return buildTestResult(terminated, config.timeout, errors.toList(), metadata)
  }

  protected fun buildTestResult(terminated: Boolean, timeout: Duration, errors: List<Throwable>, metadata: Map<String, Any?>): TestResult {
    return if (!terminated) {
      TestResult(TestStatus.Error, TestTimedOutException(timeout.seconds, TimeUnit.SECONDS), null, metadata)
    } else {
      val first = errors.firstOrNull()
      when (first) {
        null -> TestResult(TestStatus.Success, null, null, metadata)
        is AssertionError -> TestResult(TestStatus.Failure, first, null, metadata)
        else -> TestResult(TestStatus.Error, first, null, metadata)
      }
    }
  }
}