package io.kotest.core.specs

import io.kotest.core.*
import io.kotest.core.tags.Tag
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestListener

typealias BeforeTest = (TestCase) -> Unit
typealias AfterTest = (TestCase, TestResult) -> Unit
typealias BeforeAll = () -> Unit
typealias AfterAll = () -> Unit

/**
 * In Kotest a [Spec] is a container of root [TestCase]s along with optional configuration and
 * callbacks related to those tests. These root tests can in turn contain nested tests.
 *
 * A spec forms a tree, with the spec itself at the root, and nested tests forming branches and leaves.
 * The actual hierachy will depend on the style of spec being built.
 *
 * A spec can be added to another spec, allowing for composability and abstraction of tests.
 *
 * @param name an optioanl root name for all tests in this spec
 * @param configure a function that can configure a SpecBuilder to re-create this spec
 * @param tests the root tests in this spec
 */
data class Spec(
   val name: String?,
   val configure: SpecBuilder.() -> Unit,
   val tests: List<TestCase>,
   val beforeTest: BeforeTest?,
   val afterTest: AfterTest?,
   val beforeAll: BeforeAll?,
   val afterAll: AfterAll?,
   val isolationMode: IsolationMode?,
   val testCaseOrder: TestCaseOrder?,
   val tags: Set<Tag>,
   val assertionMode: AssertionMode?,
   val listeners: List<TestListener>,
   val extensions: List<SpecLevelExtension>
)

/**
 * Merges two specs. The receiver of the function takes priority for root name, isolationMode, testCaseOrder and
 * assertionMode in cases where those values are specified in both the receiver and the parameter.
 */
operator fun Spec.plus(other: Spec): Spec {
   return Spec(
      name = this.name ?: other.name,
      configure = {
         with(this@plus) {
            configure()
         }
         with(other) {
            configure()
         }
      },
      tests = this.tests + other.tests,
      beforeTest = {
         this.beforeTest?.invoke(it)
         other.beforeTest?.invoke(it)
      },
      afterTest = { testCase, testResult ->
         this.afterTest?.invoke(testCase, testResult)
         other.afterTest?.invoke(testCase, testResult)
      },
      beforeAll = {
         this.beforeAll?.invoke()
         other.beforeAll?.invoke()
      },
      afterAll = {
         this.afterAll?.invoke()
         other.afterAll?.invoke()
      },
      isolationMode = this.isolationMode ?: other.isolationMode,
      testCaseOrder = this.testCaseOrder ?: other.testCaseOrder,
      tags = this.tags + other.tags,
      assertionMode = this.assertionMode ?: other.assertionMode,
      listeners = this.listeners + other.listeners,
      extensions = this.extensions + other.extensions
   )
}
