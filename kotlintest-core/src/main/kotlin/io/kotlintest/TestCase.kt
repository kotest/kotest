package io.kotlintest

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
    // the first line number of the test
    val line: Int,
    val type: TestType,
    // config used when running the test, such as number of
    // invocations, number of threads, etc
    val config: TestCaseConfig) {
  val name = description.name
}

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
                      val metaData: Map<String, Any?> = emptyMap()) {
  companion object {
    val Success = TestResult(TestStatus.Success, null, null)
    val Ignored = TestResult(TestStatus.Ignored, null, null)
    fun failure(e: AssertionError) = TestResult(TestStatus.Failure, e, null)
    fun error(t: Throwable) = TestResult(TestStatus.Error, t, null)
    fun ignored(reason: String?) = TestResult(TestStatus.Ignored, null, reason)
  }
}

fun lineNumber(): Int {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotlintest")
  }[0].lineNumber
}