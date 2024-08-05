package io.kotest.property.core

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Controls iterations of a property test.
 */
fun interface Constraints {

   fun evaluate(context: Iteration): Boolean

   companion object {

      /**
       * Returns a [Constraints] that executes the property test for a fixed number of iterations.
       */
      fun iterations(k: Int) = Constraints { it.iteration < k }

      /**
       * Returns a [Constraints] that executes the property test for a certain duration.
       */
      fun duration(duration: Duration) = object : Constraints {
         val mark = TimeSource.Monotonic.markNow() + duration // Property tests cannot use virtual time
         override fun evaluate(context: Iteration): Boolean {
            return mark.hasNotPassedNow()
         }
      }
   }
}

data class Iteration(val iteration: Int)

fun Constraints.and(other: Constraints) = Constraints { this@and.evaluate(it) && other.evaluate(it) }

fun Constraints.or(other: Constraints) = Constraints { this@or.evaluate(it) || other.evaluate(it) }
