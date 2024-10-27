package io.kotest.property.core

import io.kotest.property.AssumptionFailedException
import io.kotest.property.core.checks.AllowCustomSeedBeforeCheck
import io.kotest.property.core.checks.FailureHandler
import io.kotest.property.core.checks.MinSuccessCheck
import io.kotest.property.core.checks.WriteSeedCheck
import io.kotest.property.core.constraints.Iteration
import kotlin.time.TimeSource

/**
 * The [PermutationExecutor] is responsible for executing a single property test with the given [PermutationConfiguration].
 */
internal class PermutationExecutor(
   private val context: PermutationContext,
) {

   private val beforeChecks = listOf(AllowCustomSeedBeforeCheck)
   private val afterChecks = listOf(MinSuccessCheck, WriteSeedCheck)

   internal suspend fun execute(
      test: suspend PermutationContext.() -> Unit
   ): PermutationResult {

      beforeChecks.forEach { it.evaluate(context) }

      var index = 0
      var discards = 0
      var successes = 0
      var failures = 0
      val start = TimeSource.Monotonic.markNow()

      while (context.constraints.evaluate(Iteration(index, start))) {

         try {

            context.registry.reset()
            context.beforePermutation()
            test(context)
            context.afterPermutation()
            successes++

         } catch (e: AssumptionFailedException) {

            // we don't mark failed assumptions as errors but we do increase discard count
            discards++

         } catch (e: Throwable) {

            failures++

            val result = IterationResult(
               iteration = index,
               success = false,
               successes = successes,
               failures = failures,
               duration = start.elapsedNow(),
               inputs = emptyList(),
               error = e
            )

            FailureHandler.handleFailure(context, result)


         }
         index++
      }

      val result = PermutationResult(
         iterations = index,
         successes = successes,
         failures = failures,
         discards = discards,
         duration = start.elapsedNow()
      )

      afterChecks.forEach { it.evaluate(context, result) }

      return result
   }

}
