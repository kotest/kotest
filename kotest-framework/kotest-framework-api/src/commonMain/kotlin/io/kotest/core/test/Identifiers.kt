package io.kotest.core.test

import io.kotest.mpp.bestName
import io.kotest.mpp.isStable

/**
 * Used to generate stable identifers for data tests and to ensure test names are unique.
 */
object Identifiers {

   /**
    * Each test name must be unique. We can use the toString if we determine the instance is stable.
    *
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [primitiveTypes].
    *
    * Note: If the user has overridden toString() and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      return if (isStable(t::class)) {
         t.toString()
      } else {
         t::class.bestName()
      }
   }

   fun uniqueTestName(name: String, testNames: Set<String>): String {
      if (!testNames.contains(name)) return name
      var n = 1
      fun nextName() = "$name ($n)"
      while (testNames.contains(nextName()))
         n++
      return nextName()
   }
}
