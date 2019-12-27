package io.kotest.property

/**
 * A [PropertyContext] is used when executing a propery test.
 * It allows feedback and tracking of the state of the property test.
 */
class PropertyContext {

   private var successes = 0
   private var failures = 0

   fun markSuccess() {
      successes++
   }

   fun markFailure() {
      failures++
   }

   fun successes() = successes
   fun failures() = failures

   fun attempts(): Int = successes + failures
}
