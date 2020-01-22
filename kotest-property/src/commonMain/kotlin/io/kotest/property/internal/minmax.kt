package io.kotest.property.internal

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import kotlin.math.min

/**
 * Given a throwable, checks that the current failure count against the max allowed failures.
 * Returns an error if the max failure rate has been hit, otherwise null.
 */
fun PropertyContext.checkMaxFailures(maxFailure: Int, t: Throwable): Throwable? = when {
   maxFailure == 0 -> t
   failures() > maxFailure -> AssertionError("Property failed ${failures()} times (maxFailure rate was ${maxFailure})")
   else -> null
}

/**
 * Checks the number of successful attempts against the min required.
 * Throws an exception if the number of successes is not sufficient.
 */
fun PropertyContext.checkMaxSuccess(config: PropTestConfig) {
   val min = min(config.minSuccess, attempts())
   if (successes() < min) {
      val e = AssertionError("Property passed ${successes()} times (minSuccess rate was $min)")
      fail(e, attempts())
   }
}
