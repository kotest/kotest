package io.kotlintest.runner.junit5

import createTestCaseInterceptorChain
import io.kotlintest.Project
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TestCaseRunner(private val listener: EngineExecutionListener) {

  // TODO beautify
  fun runTest(descriptor: TestCaseDescriptor) {
    if (descriptor.testCase.isActive()) {

      val context = AccumulatingTestContext(descriptor.testCase)

      val executor =
          if (descriptor.testCase.config.threads < 2) Executors.newSingleThreadExecutor()
          else Executors.newFixedThreadPool(descriptor.testCase.config.threads)
      listener.executionStarted(descriptor)

      val initialInterceptor = { next: () -> Unit -> descriptor.testCase.spec.interceptTestCase(descriptor.testCase, next) }
      val extensions = descriptor.testCase.config.extensions + descriptor.testCase.spec.testCaseExtensions() + Project.testCaseExtensions()
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
        listener.executionFinished(descriptor, TestExecutionResult.failed(error))
      } else if (errors.isEmpty()) {
        listener.executionFinished(descriptor, TestExecutionResult.successful())
      } else {
        listener.executionFinished(descriptor, TestExecutionResult.failed(errors.first()))
      }

    } else {
      listener.executionSkipped(descriptor, "Ignored")
    }
  }
}