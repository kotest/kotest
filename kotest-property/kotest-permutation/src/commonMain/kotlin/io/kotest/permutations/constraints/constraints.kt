package io.kotest.permutations.constraints

import kotlin.time.Duration
import kotlin.time.TimeMark

/**
 * Controls iterations of a property test.
 */
fun interface Constraints {

   /**
    * Return true if the property test should continue, given the current iteration.
    */
   fun evaluate(iteration: Iteration): Boolean

   companion object {

      /**
       * Returns a [Constraints] that executes the property test for a fixed number of iterations.
       */
      fun iterations(k: Int) = Constraints { it.iteration < k }

      /**
       * Returns a [Constraints] that executes the property test until a fixed duration has passed.
       */
      fun duration(duration: Duration) = Constraints { iteration -> (iteration.mark + duration).hasNotPassedNow() }
   }
}

data class Iteration(
   val iteration: Int, // current iteration count
   val mark: TimeMark, // timemark when the property test started
)

fun Constraints.and(other: Constraints) = Constraints { this@and.evaluate(it) && other.evaluate(it) }

fun Constraints.or(other: Constraints) = Constraints { this@or.evaluate(it) || other.evaluate(it) }
