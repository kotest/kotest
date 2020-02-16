package io.kotest.core.spec

import io.kotest.core.config.Project
import io.kotest.core.extensions.Extension
import io.kotest.core.factory.generate
import io.kotest.core.listeners.RootTest
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult

/**
 * Returns the resolved listeners for a given [Spec].
 * That is, the listeners defined directly on the spec, listeners generated from the
 * callback-dsl methods, and listeners defined in any included test factories.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   val callbacks = object : TestListener {
      override suspend fun afterSpec(spec: Spec) {
         this@resolvedTestListeners.afterSpec(spec)
      }

      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         this@resolvedTestListeners.afterTest(testCase, result)
      }

      override suspend fun beforeTest(testCase: TestCase) {
         this@resolvedTestListeners.beforeTest(testCase)
      }

      override suspend fun beforeSpec(spec: Spec) {
         this@resolvedTestListeners.beforeSpec(spec)
      }
   }
   return this._listeners + this.listeners() + callbacks + factories.flatMap { it.listeners }
}

fun Spec.resolvedExtensions(): List<Extension> {
   return this._extensions + this.extensions() + factories.flatMap { it.extensions }
}

fun Spec.resolvedTestCaseOrder() =
   this.testOrder ?: this.testCaseOrder() ?: Project.testCaseOrder()

fun Spec.resolvedIsolationMode() =
   this.isolation ?: this.isolationMode() ?: Project.isolationMode()

/**
 * Returns the root tests from this Spec and any included factories.
 * The returned list will be ordered according to the [TestCaseOrder] returned by
 * project config, or if not defined, the kotest default.
 */
fun Spec.materializeRootTests(): List<RootTest> {

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
