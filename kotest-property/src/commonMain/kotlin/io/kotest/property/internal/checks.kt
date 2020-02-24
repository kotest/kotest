package io.kotest.property.internal

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import kotlin.math.min

fun PropertyContext.checkMaxSuccess(config: PropTestConfig, seed: Long) {
   val min = min(config.minSuccess, attempts())
   if (successes() < min) {
      val e = AssertionError("Property passed ${successes()} times (minSuccess rate was $min)")
      throwPropertyTestAssertionError(e, attempts(), seed)
   }
}
