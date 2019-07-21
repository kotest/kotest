package io.kotlintest

import java.time.Duration

/**
 * A [TestCase] describes an actual block of code that will be tested.
 * It contains a reference back to the [Spec] instance in which it
 * is being executed.
 *
 * It also captures a closure of the body of the test case.
 * This is a function which is invoked with a [TestContext].
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [Spec] instance is created.
 *
 * A test can be nested inside other tests if the [Spec] supports it.
 *
 * For example, in the FunSpec we only allow top level tests.
 *
 * test("this is a test") { }
 *
 * And in WordSpec we allow two levels of tests.
 *
 * "a string" should {
 *   "return the length" {
 *   }
 * }
 *
 */
data class TestCase(
    // the description contains the names of all parents, plus the name of this test case
    val description: Description,
    // the spec that contains this testcase
    val spec: Spec,
    // a closure of the test function
    val test: suspend TestContext.() -> Unit,
    val source: SourceRef,
    val type: TestType,
    // config used when running the test, such as number of
    // invocations, threads, etc
    val config: TestCaseConfig) {

  val name = description.name
  fun isFocused() = name.startsWith("f:")
  fun isTopLevel(): Boolean = description.isTopLevel()
  fun isBang(): Boolean = name.startsWith("!")

  // for compatiblity with earlier plugins
  fun getLine(): Int = source.lineNumber

  companion object {
    fun test(description: Description, spec: Spec, test: suspend TestContext.() -> Unit): TestCase =
        TestCase(description, spec, test, sourceRef(), TestType.Test, TestCaseConfig())

    fun container(description: Description, spec: Spec, test: suspend TestContext.() -> Unit): TestCase =
        TestCase(description, spec, test, sourceRef(), TestType.Container, TestCaseConfig())
  }
}

data class SourceRef(val lineNumber: Int, val fileName: String)

enum class TestType {
  Container, Test
}

enum class TestStatus {
  // the test was skipped completely
  Ignored,
  // the test was successful
  Success,
  // the test failed because of some exception that was not an assertion error
  Error,
  // the test ran but an assertion failed
  Failure
}

data class TestResult(val status: TestStatus,
                      val error: Throwable?,
                      val reason: String?,
                      val duration: Duration,
                      val metaData: Map<String, Any?> = emptyMap()) {
  companion object {
    fun success(duration: Duration) = TestResult(TestStatus.Success, null, null, duration)
    val Ignored = TestResult(TestStatus.Ignored, null, null, Duration.ZERO)
    fun failure(e: AssertionError, duration: Duration) = TestResult(TestStatus.Failure, e, null, duration)
    fun error(t: Throwable, duration: Duration) = TestResult(TestStatus.Error, t, null, duration)
    fun ignored(reason: String?) = TestResult(TestStatus.Ignored, null, reason, Duration.ZERO)
  }
}

fun sourceRef(): SourceRef {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotlintest")
  }[0].run { SourceRef(lineNumber, fileName) }
}

/**
 * Exception to mark a test as ignored while it is already running
 *
 * The SkipTestException may be thrown inside a test case to skip it (mark it as ignored). Any subclass of this class
 * may be used, in case you want to use your specific exception.
 *
 * ```
 * class FooTest : StringSpec({
 *    "Ignore this test!" {
 *        throw SkipTestException("I want to ignore this test!")
 *    }
 * })
 * ```
 */
open class SkipTestException(val reason: String? = null): RuntimeException(reason)