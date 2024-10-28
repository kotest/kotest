package io.kotest.permutations

import io.kotest.permutations.statistics.Classifications
import io.kotest.property.RandomSource
import io.kotest.property.statistics.Label

/**
 * [Permutation] is the runtime receiver of a permutation test that allows for the collection of statistics.
 */
class Permutation(
   val iteration: Int,
   val rs: RandomSource,
   private val classifications: Classifications,
) {

   private fun classify(label: Label?, classification: Any?) {
      val stats = classifications.counts.getOrPut(label) { mutableMapOf() }
      val count = stats.getOrElse(classification) { 0 }
      stats[classification] = count + 1
   }

   /**
    * Adds a classification to this permutation.
    */
   fun classify(classification: Any?) {
      classify(null, classification)
   }

   /**
    * Adds a classification to this permutation.
    */
   fun classify(predicate: Boolean, ifTrue: Any?, ifFalse: Any?) {
      classify(if (predicate) ifTrue else ifFalse)
   }

   /**
    * Adds a classification to this permutation with the given label.
    */
   fun classify(label: String, classification: Any?) {
      classify(Label(label), classification)
   }

   /**
    * Adds a classification to this permutation.
    */
   fun classify(label: String, predicate: Boolean, ifTrue: Any?, ifFalse: Any?) {
      classify(label, if (predicate) ifTrue else ifFalse)
   }
}
