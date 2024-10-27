package io.kotest.permutations

import io.kotest.permutations.statistics.Statistics
import io.kotest.property.RandomSource
import io.kotest.property.statistics.Label

/**
 * [Permutation] is the runtime receiver of a permutation test that allows for the collection of statistics.
 */
class Permutation(
   val iteration: Int,
   val rs: RandomSource,
   val statistics: Statistics,
) {

   private fun classify(label: Label?, classification: Any?) {
//      val stats = statistics.getOrPut(label) { mutableMapOf() }
//      val count = stats.getOrElse(classification) { 0 }
//      stats[classification] = count + 1
   }

   /**
    * Adds a classification to this permutation.
    */
   fun classify(classification: Any?) {
      classify(null, classification)
   }

   /**
    * Adds a classification to this permutation with the given label.
    */
   fun classify(label: String, classification: Any?) {
      classify(Label(label), classification)
   }

}
