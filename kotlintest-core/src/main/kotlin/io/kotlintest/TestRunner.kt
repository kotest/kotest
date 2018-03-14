package io.kotlintest

import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.Executors

fun <CONTEXT> createInterceptorChain(
    interceptors: Iterable<(CONTEXT, () -> Unit) -> Unit>,
    initialInterceptor: (CONTEXT, () -> Unit) -> Unit): (CONTEXT, () -> Unit) -> Unit {
  return interceptors.reversed().fold(initialInterceptor) { a, b ->
    { context: CONTEXT, fn: () -> Unit ->
      b(context, { a.invoke(context, { fn() }) })
    }
  }
}

class TestRunner(private val listener: EngineExecutionListener) {

  // TODO beautify
  fun runTest(spec: Spec, testCase: TestCaseDescriptor) {
    if (testCase.isActive) {
      val executor =
          if (testCase.config.threads < 2) Executors.newSingleThreadExecutor()
          else Executors.newFixedThreadPool(testCase.config.threads)
      listener.executionStarted(testCase)
      val initialInterceptor = { context: TestCaseContext, test: () -> Unit ->
        spec.interceptTestCase(context, { test() })
      }
      val testInterceptorChain = createInterceptorChain(testCase.config.interceptors, initialInterceptor)
      val testCaseContext = TestCaseContext(spec, testCase)

      val errors = mutableListOf<Throwable>()

      for (j in 1..testCase.config.invocations) {
        executor.execute {
          try {
            testInterceptorChain(testCaseContext, { testCase.test() })
          } catch (t: Throwable) {
            errors.add(t)
          }
        }
      }

      executor.shutdown()
      val timeout = testCase.config.timeout
      val terminated = executor.awaitTermination(timeout.amount, timeout.timeUnit)

      if (!terminated) {
        val error = TestTimedOutException(timeout.amount, timeout.timeUnit)
        listener.executionFinished(testCase, TestExecutionResult.failed(error))
      } else if (errors.isEmpty()) {
        listener.executionFinished(testCase, TestExecutionResult.successful())
      } else {
        listener.executionFinished(testCase, TestExecutionResult.failed(errors.first()))
      }

    } else {
      listener.executionSkipped(testCase, "Ignored")
    }
  }
}