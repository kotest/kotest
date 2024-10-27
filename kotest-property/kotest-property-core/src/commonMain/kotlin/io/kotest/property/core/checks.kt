package io.kotest.property.core

import io.kotest.property.MaxDiscardPercentageException
import kotlin.math.min


/**
 * Checks that the number of values discarded by assumptions is less than the configured max discard percentage.
 */
internal fun checkMaxDiscards(context: PermutationConfiguration, result: PermutationResult) {

   if (result.discards > context.maxDiscardPercentage) {
      throw MaxDiscardPercentageException(result.discards, context.maxDiscardPercentage)
   }
}
