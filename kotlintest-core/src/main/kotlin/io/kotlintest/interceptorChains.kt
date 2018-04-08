@file:Suppress("DEPRECATION")

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.TestCaseExtension

fun createSpecInterceptorChain(
    spec: Spec,
    extensions: Iterable<SpecExtension>,
    initial: (() -> Unit) -> Unit): (() -> Unit) -> Unit {
  return extensions.reversed().fold(initial) { a, extension ->
    { fn: () -> Unit ->
      extension.intercept(spec, { a.invoke(fn) })
    }
  }
}

fun createTestCaseInterceptorChain(testCase: TestCase,
                                   extensions: Iterable<TestCaseExtension>,
                                   initial: (TestCaseConfig) -> TestResult): (TestCaseConfig) -> TestResult {
  return extensions.fold(initial, { fn, extension ->
    { config: TestCaseConfig ->
      extension.intercept(testCase.description, testCase.spec, config, fn)
    }
  })
}
