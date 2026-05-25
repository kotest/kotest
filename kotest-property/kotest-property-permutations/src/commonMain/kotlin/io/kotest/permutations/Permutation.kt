package io.kotest.permutations

import io.kotest.engine.IterationSkippedException
import io.kotest.property.RandomSource

/**
 * A [Permutation] is the runtime context used by each invocation of the permutation.
 *
 * It allows for collecting statistics about the values and for
 * adding assumptions to the test for each iteration.
 */
class Permutation(
   val iteration: Int,
   val rs: RandomSource,
   private val classifications: Classifications,
) {

   /**
    * Adds an assumption to the test by checking for an [AssertionError].
    *
    * If the [assumptions] function throws an [AssertionError] that permutation is discarded.
    * For example, any Kotest assertion functions can be used inside this function to validate values.
    */
   fun assume(assumptions: () -> Unit) {
      try {
         assumptions()
      } catch (_: AssertionError) {
         throw IterationSkippedException()
      }
   }

   /**
    * Adds a simple assumption to the test.
    *
    * If the [predicate] is false, that permutation is discarded.
    */
   fun assume(predicate: Boolean) {
      if (!predicate) throw IterationSkippedException()
   }

   private fun classify(label: Label, value: Any?) {
      if (value == null) return
      val stats = classifications.counts.getOrPut(label) { mutableMapOf() }
      val count = stats.getOrElse(value) { 0 }
      stats[value] = count + 1
   }

   /**
    * Adds a classification to this permutation using the default label.
    */
   fun classify(value: Any?) {
      classify(Label.Default, value)
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
   fun classify(label: String, value: Any?) {
      classify(Label(label), value)
   }

   /**
    * Adds a classification to this permutation.
    */
   fun classify(label: String, predicate: Boolean, ifTrue: Any?, ifFalse: Any?) {
      classify(label, if (predicate) ifTrue else ifFalse)
   }
}

/**
 * A label is a string that can be used to group together classifications.
 *
 * For example, you may wish to classify a value by even and odd, and also by positive and negative.
 * To do this, you could use two labels: "parity" which would track the even or oddness of the value, and
 * another label "sign" which tracks if positive or negative.
 */
data class Label(val value: String) {
   companion object {
      val Default = Label("statistics")
   }
}

/**
 * Tracks the counts of classifications for each label and value as provided by `classify` calls.
 */
class Classifications(
   val counts: MutableMap<Label, MutableMap<Any, Int>> = mutableMapOf()
)
