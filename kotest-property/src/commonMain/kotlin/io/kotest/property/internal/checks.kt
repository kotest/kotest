package io.kotest.property.internal

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting
import kotlin.math.min

internal fun PropTestConfig.checkFailOnSeed() {
   if (seed != null && PropertyTesting.failOnSeed)
      error("A seed is specified on this property-test and failOnSeed is true")
}

/**
 * Checks that the number of times this property text has passed is at least the
 * configured min success rate.
 */
fun PropertyContext.checkMinSuccess(config: PropTestConfig, seed: Long) {
   val min = min(config.minSuccess, attempts())
   if (successes() < min) {
      val error = "Property passed ${successes()} times (minSuccess rate was $min)\n"
      throwPropertyTestAssertionError(AssertionError(error), attempts(), seed)
   }
}
