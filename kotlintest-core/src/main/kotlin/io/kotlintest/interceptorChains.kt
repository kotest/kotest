import io.kotlintest.Spec
import io.kotlintest.TestCase
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

fun createTestCaseInterceptorChain(
    testCase: TestCase,
    extensions: Iterable<TestCaseExtension>,
    initial: (() -> Unit) -> Unit): (() -> Unit) -> Unit {
  return extensions.reversed().fold(initial) { a, extension ->
    { fn: () -> Unit ->
      extension.intercept(testCase, { a.invoke(fn) })
    }
  }
}