package io.kotest.property

suspend fun PropertyContext.withAssumption(
   predicate: Boolean,
   test: suspend () -> Unit,
) {
   if (predicate) {
      assumptionPredicateTrue()
      test()
   } else {
      assumptionPredicateFalse()
   }
}

internal fun PropertyContext.checkMaxDiscards() {
   if (discardPercentage() > config.maxDiscardPercentage) {
      throw MaxDiscardPercentageException(discardPercentage(), config.maxDiscardPercentage)
   }
}

class MaxDiscardPercentageException(discardPercentage: Int, maxDiscardPercentage: Int) :
   Exception("Percentage of discarded inputs ($discardPercentage%) exceeds max ($maxDiscardPercentage%). Adjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in property test config.")
