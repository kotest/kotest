package io.kotest.property.core

import io.kotest.property.AssumptionFailedException
import io.kotest.property.internal.throwPropertyTestAssertionError

internal suspend fun executePropTest(context: PermutationContext, test: suspend EvaluationContextToBeRenamed.() -> Unit) {

   if (context.seed != null && context.failOnSeed)
      error("A seed is specified on this property test and failOnSeed is true")

   val constraints = context.constraints
      ?: context.duration?.let { Constraints.duration(it) }
      ?: Constraints.iterations(context.iterations)

   var k = 0
   while (constraints.evaluate(Iteration(k))) {
      try {
         GenDelegateRegistry.reset()
         context.beforePermutation()
         test(EvaluationContextToBeRenamed(0))
         context.afterPermutation()
      } catch (e: AssumptionFailedException) {
         // we don't mark failed assumptions as errors
      } catch (e: Throwable) {
         // we track any throwables and try to shrink them
         val shrinks = shrink(context, test)
         throwPropertyTestAssertionError(shrinks, AssertionError(e), k, context.rs.seed)
      }
      k++
   }
}
