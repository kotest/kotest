package io.kotest.runner.jvm.spec

import io.kotest.Project
import io.kotest.core.TestCase
import io.kotest.core.TestCaseOrder
import io.kotest.core.description
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.generate
import io.kotest.extensions.TopLevelTest
import io.kotest.fp.Try

/**
 * Returns the root tests for the receiver [SpecConfiguration].
 *
 * This includes tests added directly to the spec at initialization, as well
 * as materializing dynamic tests added instances of [TestFactory].
 */
fun SpecConfiguration.materializeRootTests(): Try<List<TopLevelTest>> = Try {
   val tests = rootTestCases + factories
      .flatMap { it.generate(this::class.description(), this) }
   tests.withIndex().map { TopLevelTest(it.value, it.index) }
}

/**
 * Orders the collection of [TestCase]s based on the [TestCaseOrder] parameter derived
 * from the spec or project default.
 */
fun List<TestCase>.ordered(spec: SpecConfiguration): List<TestCase> {
   return when (spec.resolvedTestCaseOrder()) {
      TestCaseOrder.Sequential -> this
      TestCaseOrder.Random -> this.shuffled()
      TestCaseOrder.Lexicographic -> this.sortedBy { it.name.toLowerCase() }
   }
}


fun SpecConfiguration.resolvedTestCaseOrder() =
   testOrder ?: testCaseOrder() ?: Project.testCaseOrder()
