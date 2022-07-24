package io.kotest.property

import io.kotest.matchers.shouldBe

suspend fun withAssumptions(
   predicate: Boolean,
   test: suspend () -> Unit,
): Unit = withAssumptions(assumptions = { predicate shouldBe true }, test)

fun assume(predicate: Boolean) {
   if (!predicate) throw AssumptionFailedException
}

suspend fun withAssumptions(
   assumptions: () -> Unit,
   test: suspend () -> Unit,
) {
   assume(assumptions)
   test()
}

fun assume(assumptions: () -> Unit) {
   try {
      assumptions()
   } catch (e: AssertionError) {
      throw AssumptionFailedException
   }
}

internal fun PropertyContext.checkMaxDiscards() {
   if (discardPercentage() > config.maxDiscardPercentage) {
      throw MaxDiscardPercentageException(discardPercentage(), config.maxDiscardPercentage)
   }
}

object AssumptionFailedException : Exception()

class MaxDiscardPercentageException(discardPercentage: Int, maxDiscardPercentage: Int) :
   Exception("Percentage of discarded inputs ($discardPercentage%) exceeds max ($maxDiscardPercentage%). Adjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in property test config.")
