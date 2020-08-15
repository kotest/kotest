package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder

/**
 * Orders the collection of [TestCase]s based on the provided [TestCaseOrder].
 */
fun List<TestCase>.ordered(spec: TestCaseOrder): List<TestCase> {
   return when (spec) {
      TestCaseOrder.Sequential -> this
      TestCaseOrder.Random -> this.shuffled()
      TestCaseOrder.Lexicographic -> this.sortedBy { it.description.displayName().toLowerCase() }
   }
}
