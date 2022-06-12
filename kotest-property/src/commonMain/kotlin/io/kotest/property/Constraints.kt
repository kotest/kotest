package io.kotest.property

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Controls iterations of a property test.
 */
fun interface Constraints {

   fun evaluate(): Boolean

   companion object {

      /**
       * Returns a [Constraints] that executes the property test for a fixed number of iterations.
       */
      fun iterations(k: Int) = object : Constraints {
         var count = 0
         override fun evaluate(): Boolean {
            val result = count < k
            count++
            return result
         }
      }

      /**
       * Returns a [Constraints] that executes the property test for a certain duration.
       */
      fun duration(duration: Duration) = object : Constraints {
         val mark = TimeSource.Monotonic.markNow().plus(duration) // TODO #3052
         override fun evaluate(): Boolean {
            return mark.hasNotPassedNow()
         }
      }
   }
}

fun Constraints.and(other: Constraints) = Constraints { this@and.evaluate() && other.evaluate() }

fun Constraints.or(other: Constraints) = Constraints { this@or.evaluate() || other.evaluate() }
