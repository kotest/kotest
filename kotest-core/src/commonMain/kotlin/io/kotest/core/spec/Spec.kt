package io.kotest.core.spec

import io.kotest.core.factory.generate
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.TestListener
import io.kotest.core.extensions.RootTest
import io.kotest.core.extensions.SpecLevelExtension
import io.kotest.fp.Tuple2

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

   return this._listeners + this.listeners() + callbacks + factories.flatMap { it.listeners }
}

fun SpecConfiguration.resolvedExtensions(): List<SpecLevelExtension> {
   return this._extensions + this.extensions()
}

fun SpecConfiguration.resolvedTestCaseOrder() =
   this.testOrder ?: this.testCaseOrder() ?: TestCaseOrder.Sequential

fun SpecConfiguration.resolvedIsolationMode() =
   this.isolation ?: this.isolationMode() ?: IsolationMode.InstancePerLeaf

/**
 * Returns the root tests from this Spec and any included factories.
 * The returned list will be ordered according to the [TestCaseOrder] returned by
 * project config, or if not defined, the kotest default.
 */
fun SpecConfiguration.materializeRootTests(): List<RootTest> {

   val order = resolvedTestCaseOrder()
   // materialize the tests in the factories at this time
   val allTests = this.rootTestCases + factories.flatMap { it.generate(this::class.description(), this) }

   // apply the configuration from this spec to each resolved test
   return allTests
      .map {
         it.copy(
            assertionMode = it.assertionMode ?: this.assertions ?: this.assertionMode(),
            config = it.config.copy(tags = it.config.tags + this._tags + this.tags())
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
