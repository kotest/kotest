package io.kotest.property.core

import io.kotest.property.RandomSource
import io.kotest.property.statistics.Label

/**
 * [Permutation] is the runtime receiver of a permutation test that allows feedback to the test runner.
 *
 * For example, to collect statistics on the classifications of the generated values or to test for assumptions.
 */
class Permutation(
   val iteration: Int,
   val rs: RandomSource,
) {

   private fun collect(label: Label?, classification: Any?) {
//      val stats = statistics.getOrPut(label) { mutableMapOf() }
//      val count = stats.getOrElse(classification) { 0 }
//      stats[classification] = count + 1
   }

   fun collect(classification: Any?) {
      collect(null, classification)
   }

   fun collect(label: String, classification: Any?) {
      collect(Label(label), classification)
   }
}
