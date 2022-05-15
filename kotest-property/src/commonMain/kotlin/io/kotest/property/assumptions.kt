package io.kotest.property

import io.kotest.matchers.shouldBe

fun interface Assume {
   operator fun invoke()
}

suspend fun PropertyContext.withAssumptions(
   predicate: Boolean,
   test: suspend () -> Unit,
): Unit = withAssumptions(assumptions = arrayOf(Assume { predicate shouldBe true }), test)

suspend fun PropertyContext.withAssumptions(
   vararg assumptions: Assume,
   test: suspend () -> Unit,
) {
   try {
      assumptions.forEach {
         it.invoke()
      }
      assumptionPredicateTrue()
      test()
   } catch (e: AssertionError) {
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
