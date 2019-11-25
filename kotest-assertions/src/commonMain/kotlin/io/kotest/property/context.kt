package io.kotest.property

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
class PropertyContext {

   private var successes = 0
   private var failures = 0

   internal fun success() {
      successes++
   }

   internal fun failure() {
      failures++
   }

   internal fun successes() = successes
   internal fun failures() = failures

   fun attempts(): Int = successes + failures
}
