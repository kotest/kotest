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
 * The [PermutationExecutor] is responsible for executing a single permutation test with the given [PermutationContext].
 */
internal class PermutationExecutor(
   private val context: PermutationContext,
) {

   internal suspend fun execute(
      test: suspend PermutationContext.() -> Unit
   ): PermutationResult {

      ConfigWriter.writeIfEnabled(context)
      AllowCustomSeedBeforeCheck.check(context)

      // generators are initialized with the random source here, since it is not available when the
      // delegates are registered
      context.registry.delegates.forEach { it.initialize(context.rs) }

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

            iterations++
            successes++

         } catch (e: AssumptionFailedException) {

            MaxDiscardCheck.ensureConfigured(context.maxDiscardPercentage)

            // we don't mark failed assumptions as errors or attempts but we do increase discard count
            discards++

            // once discards have hit the discard threshold, we start to test the max discard percentage check
            MaxDiscardCheck.check(context, discards, iterations)

         } catch (e: Throwable) {

            iterations++
            failures++

            val result = IterationFailure(
               iteration = iterations,
               success = false,
               successes = successes,
               failures = failures,
               duration = mark.elapsedNow(),
               inputs = context.registry.samples().map { it.value },
               error = e,
            )

            // we might be able to tolerate this failure, if max failure is set > 0 and we haven't hit it yet
            // otherwise, this test will now throw an exception and do the failure state operations
            if (failures > context.maxFailures) {
               SeedOperations.writeFailedSeed(context.writeFailedSeed, context.rs.seed)
               ClassificationsWriter.writeIfEnabled(context, true)
               FailureHandler.handleFailure(context, result)
            }
         }
      }

      val result = PermutationResult(
         attempts = iterations,
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
