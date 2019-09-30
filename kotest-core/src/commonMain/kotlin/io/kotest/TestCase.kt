package io.kotest

import io.kotest.core.SourceRef
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.sourceRef

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

