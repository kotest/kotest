package io.kotest.permutations

import io.kotest.permutations.checks.AllowCustomSeedBeforeCheck
import io.kotest.permutations.statistics.CoverageCheck
import io.kotest.permutations.checks.FailureHandler
import io.kotest.permutations.checks.MaxDiscardCheck
import io.kotest.permutations.checks.MinSuccessCheck
import io.kotest.permutations.constraints.Iteration
import io.kotest.permutations.seeds.SeedOperations
import io.kotest.permutations.statistics.ClassificationsWriter
import io.kotest.property.AssumptionFailedException
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

      ConfigWriter.writeIfEnabled(context)
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

            // eagerly check if we should stop because we've hit the max discards
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
               ClassificationsWriter.writeIfEnabled(context, true)
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

      ClassificationsWriter.writeIfEnabled(context, true)
      MinSuccessCheck.check(context, result)
      CoverageCheck.check(context, result)

      // at this point the test can't fail, so we can clear the seed
      SeedOperations.clearFailedSeed()

      return result
   }
}
