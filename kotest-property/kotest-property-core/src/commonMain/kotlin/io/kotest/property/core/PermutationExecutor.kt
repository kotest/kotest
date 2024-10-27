package io.kotest.property.core

import io.kotest.property.AssumptionFailedException
import io.kotest.property.core.checks.AllowCustomSeedBeforeCheck
import io.kotest.property.core.checks.FailureHandler
import io.kotest.property.core.checks.MaxDiscardCheck
import io.kotest.property.core.checks.MinSuccessCheck
import io.kotest.property.core.constraints.Iteration
import kotlin.time.TimeSource

/**
 * The [PermutationExecutor] is responsible for executing a single property test with the given [PermutationContext].
 */
internal class PermutationExecutor(
   private val context: PermutationContext,
) {

   internal suspend fun execute(
      test: suspend PermutationContext.() -> Unit
   ): PermutationResult {

      AllowCustomSeedBeforeCheck.check(context)

      var iterations = 0
      var discards = 0
      var successes = 0
      var failures = 0
      val mark = TimeSource.Monotonic.markNow()

      while (context.constraints.evaluate(Iteration(iterations, mark))) {

         try {

            context.registry.reset()
            context.beforePermutation()
            test(context)
            context.afterPermutation()
            successes++
            iterations++

         } catch (e: AssumptionFailedException) {

            // we don't mark failed assumptions as errors but we do increase discard count
            discards++

            // eagerly check if we should stop
            MaxDiscardCheck.check(context, discards, iterations)

         } catch (e: Throwable) {

            failures++
            iterations++

            val result = IterationResult(
               iteration = iterations,
               success = false,
               successes = successes,
               failures = failures,
               duration = mark.elapsedNow(),
               inputs = context.registry.samples().map { it.value },
               error = e
            )

            // we might be able to tolerate this failure, if max failure is set > 0,
            // otherwise, this test will now throw an exception
            if (failures > context.maxFailures) {
               FailureHandler.handleFailure(context, result)
            }
         }
      }

      val result = PermutationResult(
         iterations = iterations,
         successes = successes,
         failures = failures,
         discards = discards,
         duration = mark.elapsedNow(),
         shrinks = emptyList(),
      )

      // ensure we have met the min success criteria
      MinSuccessCheck.check(context, result)

      return result
   }
}
