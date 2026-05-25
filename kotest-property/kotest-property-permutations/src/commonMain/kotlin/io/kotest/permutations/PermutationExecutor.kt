@file:OptIn(ExperimentalKotest::class)

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.IterationSkippedException
import io.kotest.permutations.checks.AllowCustomSeedBeforeCheck
import io.kotest.permutations.checks.MaxDiscardCheck
import io.kotest.permutations.checks.MinSuccessCheck
import io.kotest.permutations.constraints.Iteration
import io.kotest.permutations.errors.FailureHandler
import io.kotest.permutations.seeds.SeedOperations
import io.kotest.permutations.statistics.ClassificationsWriter
import io.kotest.permutations.statistics.CoverageCheck
import kotlin.time.TimeSource

/**
 * The [PermutationExecutor] is responsible for executing a single iteration of a permutation test
 * with the given [PermutationContext].
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

      var invocations = 0
      var attempts = 0
      var discards = 0
      var successes = 0
      var failures = 0
      val mark = TimeSource.Monotonic.markNow()

      while (context.constraints.evaluate(Iteration(invocations, mark))) {

         try {

            // always includes everything, including discarded
            invocations++

            context.registry.reset()
            context.beforePermutation()
            test(context)
            context.afterPermutation()

            successes++ // we know the test was successful
            attempts++ // since the test was successful, it wasn't skipped

         } catch (_: IterationSkippedException) {

            MaxDiscardCheck.ensureConfigured(context.maxDiscardPercentage)

            // we don't mark failed assumptions as errors or attempts, but we do increase discard count
            discards++

            // once discards have hit the discard threshold, we start to test the max discard percentage check
            MaxDiscardCheck.check(context, discards, invocations)

         } catch (e: Throwable) {

            failures++ // we know the test failed
            attempts++ // since the test failed, it wasn't skipped

            val result = IterationFailure(
               iteration = invocations,
               success = false,
               successes = successes,
               failures = failures,
               duration = mark.elapsedNow(),
               inputs = context.registry.inputs(),
               error = e,
            )

            // we might be able to tolerate this failure if maxFailures is > 0, and we haven't hit that limit yet
            // otherwise, this permutation test will now terminate, and we invoke the failure state operations
            if (failures > context.maxFailures) {
               SeedOperations.writeFailedSeed(context.writeFailedSeed, context.rs.seed)
               ClassificationsWriter.writeIfEnabled(context, false, attempts, context.classifications)
               FailureHandler.handleFailure(context, result)
            }
         }
      }

      val result = PermutationResult(
         invocations = invocations,
         attempts = attempts,
         successes = successes,
         failures = failures,
         discards = discards,
         duration = mark.elapsedNow(),
         shrinks = emptyList(),
         classifications = Classifications(),
      )

      ClassificationsWriter.writeIfEnabled(context, true, attempts, context.classifications)
      MinSuccessCheck.check(context, result)
      CoverageCheck.check(context, result)

      // at this point the test didn't fail, so we can clear any previous written seeds
      SeedOperations.clearFailedSeed()

      return result
   }
}
