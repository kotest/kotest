package io.kotlintest.runner.junit5

import createTestCaseInterceptorChain
import io.kotlintest.Project
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestListener
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TestCaseRunner(private val engineListener: EngineExecutionListener, val testListeners: List<TestListener>) {

  // TODO beautify
  fun runTest(descriptor: TestCaseDescriptor) {
    if (descriptor.testCase.isActive()) {

      val context = AccumulatingTestContext(descriptor.testCase)

      val executor =
          if (descriptor.testCase.config.threads < 2) Executors.newSingleThreadExecutor()
          else Executors.newFixedThreadPool(descriptor.testCase.config.threads)
      engineListener.executionStarted(descriptor)
      testListeners.forEach { it.testStarted(descriptor.testCase.description) }

      val initialInterceptor = { next: () -> Unit -> descriptor.testCase.spec.interceptTestCase(descriptor.testCase, next) }
      val extensions = descriptor.testCase.config.extensions + descriptor.testCase.spec.testCaseExtensions() + Project.testCaseInterceptors()
      val chain = createTestCaseInterceptorChain(descriptor.testCase, extensions, initialInterceptor)

      val errors = mutableListOf<Throwable>()
      for (j in 1..descriptor.testCase.config.invocations) {
        executor.execute {
          try {
            chain { descriptor.testCase.test(context) }
            val result = context.future().get()
            if (result != null)
              errors.add(result)
          } catch (t: Throwable) {
            errors.add(t)
          }
        }
      }

      executor.shutdown()
      val timeout = descriptor.testCase.config.timeout
      val terminated = executor.awaitTermination(timeout.seconds, TimeUnit.SECONDS)

      if (!terminated) {
        val error = TestTimedOutException(timeout.seconds, TimeUnit.SECONDS)
        engineListener.executionFinished(descriptor, TestExecutionResult.failed(error))
        testListeners.forEach { it.testFinished(descriptor.testCase.description, TestResult(TestStatus.Failed, error)) }
      } else if (errors.isEmpty()) {
        engineListener.executionFinished(descriptor, TestExecutionResult.successful())
        testListeners.forEach { it.testFinished(descriptor.testCase.description, TestResult(TestStatus.Passed, null)) }
      } else {
        engineListener.executionFinished(descriptor, TestExecutionResult.failed(errors.first()))
        testListeners.forEach { it.testFinished(descriptor.testCase.description, TestResult(TestStatus.Failed, errors.first())) }
      }

    } else {
      engineListener.executionSkipped(descriptor, "Ignored")
      testListeners.forEach { it.testFinished(descriptor.testCase.description, TestResult(TestStatus.Ignored, null)) }
    }
  }
}