package io.kotest.core.test

import io.kotest.mpp.bestName
import io.kotest.mpp.isStable

/**
 * Used to generate stable identifiers for data tests and to ensure test names are unique.
 */
object Identifiers {

   /**
    * Each test name must be unique. We can use the toString if we determine the instance is stable.
    *
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [io.kotest.mpp.primitiveTypes]. Or if the type of instance is annotated with [io.kotest.datatest.IsStableType].
    *
    * Note: If the user has overridden `toString()` and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      return if (isStable(t::class)) {
         t.toString()
      } else {
         t::class.bestName()
      }
   }
}
