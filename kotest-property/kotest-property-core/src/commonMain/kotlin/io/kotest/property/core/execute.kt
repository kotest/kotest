package io.kotest.property.core

import io.kotest.property.AssumptionFailedException
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.internal.ShrinkResult
import io.kotest.property.internal.buildMaxFailureErrorMessage
import io.kotest.property.internal.printFailureMessage
import io.kotest.property.internal.throwPropertyTestAssertionError
import io.kotest.property.seed.writeFailedSeed

internal suspend fun executePropTest(
   context: PermutationContext,
   test: suspend EvaluationContextToBeRenamed.() -> Unit
): PermutationResult {

   if (context.seed != null && context.failOnSeed)
      error("A seed is specified on this property test and failOnSeed is true")

   val constraints = context.constraints
      ?: context.duration?.let { Constraints.duration(it) }
      ?: Constraints.iterations(context.iterations)

   var evals = 0
   var discards = 0
   var successes = 0
   while (constraints.evaluate(Iteration(evals))) {
      try {
         context.registry.reset()
         context.beforePermutation()
         test(EvaluationContextToBeRenamed(0))
         context.afterPermutation()
         successes++
      } catch (e: AssumptionFailedException) {
         // we don't mark failed assumptions as errors
         discards++
      } catch (e: Throwable) {
         handleException(context, context.shrinkfn, emptyList(), context.rs.seed, e, context.config)
         // we track any throwables and try to shrink them
         val shrinks = shrink(context, test)
         throwPropertyTestAssertionError(shrinks, AssertionError(e), evals, context.rs.seed)
      }
      evals++
   }

   return PermutationResult(evaluations = evals, successes = successes, discards = discards, seed = context.rs.seed)
}

internal suspend fun handleException(
   context: PropertyContext,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   seed: Long,
   e: Throwable,
   config: PropTestConfig
) {
   if (config.maxFailure == 0) {
      writeFailedSeed(seed)
      printFailureMessage(context, inputs, e)
      throwPropertyTestAssertionError(shrinkfn(), e, context.attempts(), seed)
   } else if (context.failures() > config.maxFailure) {
      val error = buildMaxFailureErrorMessage(context, config, inputs)
      throwPropertyTestAssertionError(shrinkfn(), AssertionError(error), context.attempts(), seed)
   }
}
