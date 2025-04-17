package io.kotest.core.project

import io.kotest.core.spec.SpecRef

/**
 * Contains the discovered specs that will be executed.
 *
 * All specs are represented as [SpecRef]s.
 */
data class TestSuite(val specs: List<SpecRef>) {
   companion object {
      val empty = TestSuite(emptyList())
   }
}
