package io.kotest.property

import io.kotest.mpp.timeInMillis

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
      fun duration(millis: Long) = object : Constraints {

         val end = timeInMillis().apply {
            if (this == 0L)
               error("unsupported on this platform")
         } + millis

         override fun evaluate(): Boolean {
            return timeInMillis() < end
         }
      }
   }
}

fun Constraints.and(other: Constraints) = Constraints { this@and.evaluate() && other.evaluate() }

fun Constraints.or(other: Constraints) = Constraints { this@or.evaluate() || other.evaluate() }




