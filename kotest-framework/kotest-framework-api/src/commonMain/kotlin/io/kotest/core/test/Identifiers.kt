package io.kotest.core.test

import io.kotest.core.datatest.IsStableType
import io.kotest.core.datatest.WithDataTestName
import io.kotest.mpp.bestName
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.isStable

/**
 * Used to generate stable identifiers for data tests and to ensure test names are unique.
 */
object Identifiers {

   /**
    * Each test name must be unique. We can use the toString if we determine the instance is stable.
    *
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [primitiveTypes]. Or if the type of instance is annotated with [IsStableType].
    *
    * If instance is a type which implements [WithDataTestName], then test name return by [dataTestName] method
    * will be consider as stableIdentifier.
    *
    * Note: If the user has overridden toString() and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      return when {
         t::class.hasAnnotation<IsStableType>() || isStable(t::class) -> t.toString()
         t is WithDataTestName -> t.dataTestName()
         else -> t::class.bestName()
      }
   }

   fun uniqueTestName(name: String, testNames: Set<String>): String {
      if (!testNames.contains(name)) return name
      var n = 1
      fun nextName() = "($n) $name"
      while (testNames.contains(nextName()))
         n++
      return nextName()
   }
}
