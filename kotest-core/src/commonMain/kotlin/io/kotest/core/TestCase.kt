package io.kotest.core

import io.kotest.Description
import io.kotest.SpecInterface
import io.kotest.core.specs.FakeSpec

/**
 * A [TestCase] describes an actual block of code that will be tested.
 * It contains a reference back to the [SpecInterface] instance in which it
 * is being executed.
 *
 * It also captures a closure of the body of the test case.
 * This is a function which is invoked with a [TestContext].
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [SpecInterface] instance is created.
 *
 * A test can be nested inside other tests if the [SpecInterface] supports it.
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
   val spec: SpecInterface,
   // a closure of the test function
   val test: suspend TestContext.() -> Unit,
   val source: SourceRef,
   val type: TestType,
   // config used when running the test, such as number of
   // invocations, threads, etc
   val config: TestCaseConfig
) {

   val name = description.name

   fun isFocused() = name.startsWith("f:")

   fun isTopLevel(): Boolean = description.isTopLevel()

   fun isBang(): Boolean = name.startsWith("!")

   // for compatibility with earlier plugins
   fun getLine(): Int = source.lineNumber

   companion object {
      fun test(description: Description, spec: SpecInterface, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(
            description,
            spec,
            test,
            sourceRef(),
            TestType.Test,
            TestCaseConfig()
         )

      fun container(description: Description, spec: SpecInterface, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(
            description,
            spec,
            test,
            sourceRef(),
            TestType.Container,
            TestCaseConfig()
         )
   }
}

/**
 * Creates a new [TestCase] with the given name, test function, config and type.
 * The receiver is used as the spec class.
 */
fun Any.createTestCase(
   name: String,
   test: suspend TestContext.() -> Unit,
   config: TestCaseConfig,
   type: TestType
) = TestCase(
   Description.fromSpecClass(this::class).append(name),
   FakeSpec(),
   test,
   sourceRef(),
   type,
   config
)

