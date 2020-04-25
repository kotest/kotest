package io.kotest.property

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
class PropertyContext {

   private var successes = 0
   private var failures = 0
   private val classifications = mutableMapOf<String, Int>()

   fun markSuccess() {
      successes++
   }

   fun markFailure() {
      failures++
   }

   fun successes() = successes
   fun failures() = failures

   fun attempts(): Int = successes + failures

   fun classifications(): Map<String, Int> = classifications.toMap()

   /**
    * Increase the count of [label].
    */
   fun classify(label: String) {
      val current = classifications.getOrElse(label) { 0 }
      classifications[label] = current + 1
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
}
