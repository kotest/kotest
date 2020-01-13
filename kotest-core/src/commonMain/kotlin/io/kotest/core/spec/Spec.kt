package io.kotest.core.spec

import io.kotest.core.factory.generate
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.extensions.TestListener
import io.kotest.extensions.RootTest
import io.kotest.fp.Tuple2

///**
// * A [Spec] is the unit of execution in Kotest. It contains one or more
// * [TestCase]s which are executed individually.  All tests in a spec must
// * pass for the spec itself to be considered passing.
// *
// * Tests can either be root level, or nested inside other tests, depending
// * on the style of spec in use.
// *
// * Specs also contain [TestListener]s and [SpecLevelExtension]s which are used
// * to hook into the test lifecycle and interface with the test engine.
// *
// * A spec can define an [IsolationMode] used to control the instantiation of
// * classes for test cases in that spec.
// *
// * A spec can define the [TestCaseOrder] which controls the ordering of the
// * execution of root level tests in that spec.
// */
//data class Spec(
//   val rootTests: List<RootTest>,
//   val listeners: List<TestListener>,
//   val extensions: List<SpecLevelExtension>,
//   val isolationMode: IsolationMode?,
//   val testCaseOrder: TestCaseOrder?
//)

/**
 * Returns the resolved listeners for a given [SpecConfiguration].
 * That is, the listeners defined directly on the spec, listeners generated from the
 * callback-dsl methods, and listeners defined in any included [TestFactory]s.
 */
fun SpecConfiguration.resolvedListeners(): List<TestListener> {

   // listeners from the spec callbacks need to be wrapped into a TestListener
   val callbacks = object : TestListener {
      override suspend fun beforeTest(testCase: TestCase) {
         this@resolvedListeners.beforeTests.forEach { it(testCase) }
         this@resolvedListeners.beforeTest(testCase)
      }

      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         this@resolvedListeners.afterTests.forEach { it(Tuple2(testCase, result)) }
         this@resolvedListeners.afterTest(testCase, result)
      }

      override fun afterSpec(spec: SpecConfiguration) {
         this@resolvedListeners.afterSpecs.forEach { it() }
         this@resolvedListeners.afterSpec(spec)
      }

      override fun beforeSpec(spec: SpecConfiguration) {
         this@resolvedListeners.beforeSpecs.forEach { it() }
         this@resolvedListeners.beforeSpec(spec)
      }
   }

   return this.listeners + this.listeners() + callbacks + factories.flatMap { it.listeners }
}

fun SpecConfiguration.resolvedTestCaseOrder() =
   this.testOrder ?: this.testCaseOrder() ?: TestCaseOrder.Sequential

fun SpecConfiguration.resolvedIsolationMode() =
   this.isolation ?: this.isolationMode() ?: IsolationMode.InstancePerLeaf

fun SpecConfiguration.materializeRootTests(): List<RootTest> {

   val order = resolvedTestCaseOrder()
   val allTests = this.rootTestCases + factories
      .flatMap { it.generate(this::class.description(), this) }

   // materialize the tests in the factories at this time
   // and apply the configuration from the spec config
   // then order by the test case order
   return allTests
      .map {
         it.copy(
            assertionMode = it.assertionMode ?: this.assertionMode ?: this.assertionMode(),
            config = it.config.copy(tags = it.config.tags + this.tags + this.tags())
         )
      }
      .ordered(order)
      .withIndex()
      .map { RootTest(it.value, it.index) }
}

/**
 * Orders the collection of [TestCase]s based on the provided [TestCaseOrder].
 */
fun List<TestCase>.ordered(spec: TestCaseOrder): List<TestCase> {
   return when (spec) {
      TestCaseOrder.Sequential -> this
      TestCaseOrder.Random -> this.shuffled()
      TestCaseOrder.Lexicographic -> this.sortedBy { it.name.toLowerCase() }
   }
}
