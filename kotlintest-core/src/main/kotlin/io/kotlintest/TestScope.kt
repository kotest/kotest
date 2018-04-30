package io.kotlintest

/**
 * Used to group together [TestCase] instances
 * for heirarchical display and execution order.
 *
 * A container has a name, which is used when outputting
 * the hierarchical location of tests.
 *
 * It also has a reference back to the parent spec
 * so that we can generate a link to the source file
 * for any given test.
 *
 * Fianlly it captures a closure of the body of the container.
 * This is a function which is invoked with a [TestContext],
 * which can, at runtime, register further [TestScope]s with the
 * test plan.
 *
 * This function is designed so that the closures which
 * are used by the spec DSLs can be executed a later
 * stage, rather than when the class is constructed.
 *
 * This allows side effects inside a container to be
 * deferred until the test engine is ready to execute
 * tests inside that particular container.
 */


/**
 * Describes an actual testcase.
 * That is, a unit of code that will be tested.
 *
 * A test case is always associated with a container,
 * called a [TestContainer]. Such a descriptor is used
 * to group together related test cases. This allows
 * hierarchical reporting and output using the rich
 * DSL of the [Spec] classes.
 */
data class TestScope(
    // the description contains the names of all parents, plus this one
    val description: Description,
    // the spec that contains this testcase
    val spec: Spec,
    // a closure of the test function
    val test: TestContext.() -> Unit,
    // the first line number of the test
    val line: Int,
    // config used when running the test, such as number of
    // invocations, number of threads, etc
    val config: TestCaseConfig) {
  val name = description.name
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

data class TestResult(val status: TestStatus, val error: Throwable?, val reason: String?, val metaData: Map<String, Any?> = emptyMap()) {
  companion object {
    val Success = TestResult(TestStatus.Success, null, null)
    val Ignored = TestResult(TestStatus.Ignored, null, null)
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