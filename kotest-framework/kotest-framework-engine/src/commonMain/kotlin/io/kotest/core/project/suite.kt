package io.kotest.core.project

import io.kotest.core.spec.SpecRef

/**
 * A Kotest [TestSuite] is a non-empty collection of specs that will be executed.
 * All specs are represented as [SpecRef]s, which are instantiated on demand.
 */
data class TestSuite(val specs: List<SpecRef>) {
   init {
      require(specs.isNotEmpty()) { "TestSuite cannot be empty" }
   }

   companion object {
      val empty = TestSuite(emptyList())
   }
}
