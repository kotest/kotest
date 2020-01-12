package io.kotest.core

import io.kotest.SpecClass
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.TestFactory
import kotlin.random.Random

/**
 * A [TestCase] describes an actual block of code that will be tested.
 * It contains a reference back to the [SpecClass] instance in which it
 * is being executed.
 *
 * It also captures a closure of the body of the test case.
 * This is a function which is invoked with a [TestContext].
 * The context is used so that the test function can, at runtime,
 * register nested tests with the test engine. This allows
 * nested tests to be executed lazily as required, rather
 * than when the [SpecClass] instance is created.
 *
 * A test can be nested inside other tests if the [SpecClass] supports it.
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
   val spec: SpecConfiguration,
   // a closure of the test function
   val test: suspend TestContext.() -> Unit,
   val source: SourceRef,
   val type: TestType,
   // config used when running the test, such as number of
   // invocations, threads, etc
   val config: TestCaseConfig = TestCaseConfig(),
   // an optional factory which is used to indicate which factory (if any) generated this test.
   val factory: TestFactory? = null,
   // assertion mode can be set to control errors/warnings in a test
   // if null, defaults will be applied
   val assertionMode: AssertionMode? = null
) {

   val name = description.name

   fun isTopLevel(): Boolean = description.isTopLevel()

   // for compatibility with earlier plugins
   fun getLine(): Int = source.lineNumber

   companion object {

      fun test(description: Description, spec: SpecConfiguration, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(description, spec, test, sourceRef(), TestType.Test, TestCaseConfig(), null, null)

      fun test(description: Description, spec: SpecClass, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(description, FakeSpecConfiguration(), test, sourceRef(), TestType.Test, TestCaseConfig(), null, null)

      fun container(description: Description, spec: SpecClass, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(
            description,
            FakeSpecConfiguration(),
            test,
            sourceRef(),
            TestType.Container,
            TestCaseConfig(),
            null,
            null
         )

      fun container(description: Description, spec: SpecConfiguration, test: suspend TestContext.() -> Unit): TestCase =
         TestCase(
            description,
            spec,
            test,
            sourceRef(),
            TestType.Container,
            TestCaseConfig(),
            null,
            null
         )
   }
}

class FakeSpecConfiguration : SpecConfiguration()

data class GeneratorId(val id: String) {
   companion object {
      fun uuid(): GeneratorId = GeneratorId(Random.Default.nextLong().toString())
   }
}
