package io.kotest.property

import io.kotest.property.statistics.Label
import kotlin.math.roundToInt

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
class PropertyContext(val config: PropTestConfig = PropTestConfig()) {

   private var successes = 0
   private var failures = 0
   private var evals = 0
   private val classifications = mutableMapOf<String, Int>()
   private val autoclassifications = mutableMapOf<String, MutableMap<String, Int>>()

   private val statistics = mutableMapOf<Label?, MutableMap<Any?, Int>>()

   fun markSuccess() {
      successes++
   }

   fun markFailure() {
      failures++
   }

   fun markEvaluation() = evals++

   fun successes() = successes
   fun failures() = failures

   /**
    * Returns the number of invocations of the test.
    * Any values bypassed due to assumptions are not included here.
    */
   fun attempts(): Int = successes + failures

   /**
    * Returns the number of times the checkAll/forAll function was invoked.
    * This is the count pre-assumption checks.
    */
   fun evals(): Int = evals

   /**
    * Returns an Int that is the rounded percentage of discarded inputs (failed assumptions) from all inputs.
    */
   fun discardPercentage(): Int {
      val discards = evals - attempts()
      return if (discards == 0 || evals() == 0) 0 else (discards / evals().toDouble() * 100.0).roundToInt()
   }

   fun classifications(): Map<String, Int> = classifications.toMap()
   fun autoclassifications(): Map<String, Map<String, Int>> = autoclassifications.toMap()

   /**
    * Increase the count of [label].
    */
   fun classify(label: String) {
      val current = classifications.getOrElse(label) { 0 }
      classifications[label] = current + 1
   }

   fun classify(input: Int, label: String) {
      val current = autoclassifications.getOrPut(input.toString()) { mutableMapOf() }
      val count = current[label] ?: 0
      current[label] = count + 1
      autoclassifications[input.toString()]
   }

   /**
    * Increase the count of [label] if [condition] is true.
    */
   fun classify(condition: Boolean, label: String) {
      if (condition) classify(label)
   }

   /**
    * Increase the count of [trueLabel] if [condition] is true, otherwise increases
    * the count of [falseLabel].
    */
   fun classify(condition: Boolean, trueLabel: String, falseLabel: String) {
      if (condition) {
         val current = classifications.getOrElse(trueLabel) { 0 }
         classifications[trueLabel] = current + 1
      } else {
         val current = classifications.getOrElse(falseLabel) { 0 }
         classifications[falseLabel] = current + 1
      }
   }

   /**
    * Returns all labels used in this property test run.
    */
   fun labels(): Set<Label> = statistics.keys.toSet().filterNotNull().toSet()

   /**
    * Returns the statistics.
    */
   fun statistics(): Map<Label?, Map<Any?, Int>> = statistics.toMap()

   fun collect(classification: Any?) {
      collect(null, classification)
   }

   fun collect(label: String, classification: Any?) {
      collect(Label(label), classification)
   }

   private fun collect(label: Label?, classification: Any?) {
      val stats = statistics.getOrPut(label) { mutableMapOf() }
      val count = stats.getOrElse(classification) { 0 }
      stats[classification] = count + 1
   }
}
