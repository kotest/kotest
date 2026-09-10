@file:OptIn(ExperimentalKotest::class)

package io.kotest.property.internal

import io.kotest.assertions.print.print
import io.kotest.common.ExperimentalKotest
import io.kotest.common.stacktrace.stacktraces
import io.kotest.engine.IterationSkippedException
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.BeforePropertyContextElement
import io.kotest.property.Classifier
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.seed.writeFailedSeed
import io.kotest.property.statistics.outputStatistics
import kotlinx.coroutines.currentCoroutineContext

/**
 * Performs a property test for a single set of values, tracking the min success and max failure rates.
 *
 * Will perform shrinking and throw when the property test is deemed to have failed.
 *
 * If a [Classifier] is provided, will classify each value.
 *
 * If registered, will invoke [io.kotest.property.lifecycle.beforeProperty] and
 * [io.kotest.property.lifecycle.afterProperty] lifecycle methods.
 */
internal suspend fun test(
   context: PropertyContext,
   config: PropTestConfig,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   classifiers: List<Classifier<out Any?>?>,
   seed: Long,
   contextualSeed: Long,
   testFn: suspend () -> Any,
) {
   require(inputs.size == classifiers.size)
   context.markEvaluation()
   context.setupContextual(RandomSource.seeded(contextualSeed))
   if (context.evals() >= config.skipTo) {
      try {

         inputs.indices.forEach { k ->
            val classifier = classifiers[k]
            if (classifier != null) {
               @Suppress("UNCHECKED_CAST")
               classifier as Classifier<Any?>
               val value = inputs[k]
               val label = classifier.classify(value)
               @Suppress("DEPRECATION")
               if (label != null) context.classify(k, label)
            }
         }

         currentCoroutineContext()[BeforePropertyContextElement]?.before?.invoke()
         testFn()
         context.markSuccess()
         currentCoroutineContext()[AfterPropertyContextElement]?.after?.invoke()
      } catch (_: IterationSkippedException) {
         // we don't mark failed assumptions as errors
      } catch (e: Throwable) {
         // we track any throwables and try to shrink them
         context.markFailure()
         outputStatistics(context, inputs.size, false)
         handleException(context, shrinkfn, inputs, seed, e, config)
      }
   } else {
      printSkippedMessage(context, inputs)
   }
}

internal suspend fun handleException(
   context: PropertyContext,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   seed: Long,
   e: Throwable,
   config: PropTestConfig
) {
   writeFailedSeed(seed)
   val evalIndex = context.evals()
   if (config.maxFailure == 0) {
      printFailureMessage(context, inputs, e)
      val (results, replayFailureNote) = runShrinkOrCaptureReplayFailure(shrinkfn)
      replayFailureNote?.let { println("Note: shrink replay was skipped — $it") }
      throwPropertyTestAssertionError(results, e, context.attempts(), seed, config.outputHexForUnprintableChars, evalIndex)
   } else if (context.failures() > config.maxFailure) {
      val error = buildMaxFailureErrorMessage(context, config, inputs)
      val (results, replayFailureNote) = runShrinkOrCaptureReplayFailure(shrinkfn)
      replayFailureNote?.let { println("Note: shrink replay was skipped — $it") }
      throwPropertyTestAssertionError(results, AssertionError(error), context.attempts(), seed, config.outputHexForUnprintableChars, evalIndex)
   }
   // Implicit third branch (maxFailure > 0 && failures <= maxFailure): allowed failure, do NOT
   // shrink. shrinkfn() is invoked only when about to throw, matching the pre-#3076 behavior.
}

/**
 * Runs [shrinkfn] (which may shrink via search or via [doReplay]) and returns the produced
 * results. If the replay path is stale or otherwise unusable, [doReplay] raises a
 * [ReplayShrinkPathException] which is caught here so callers can surface a regular property
 * failure with a "replay was skipped" note rather than leaking the internal exception.
 */
private suspend fun runShrinkOrCaptureReplayFailure(
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>
): Pair<List<ShrinkResult<Any?>>, String?> = try {
   shrinkfn() to null
} catch (replayEx: ReplayShrinkPathException) {
   emptyList<ShrinkResult<Any?>>() to replayEx.message
}

fun printFailureMessage(
   context: PropertyContext,
   inputs: List<Any?>,
   e: Throwable,
) {
   println(
      buildString {
         appendLine("Property test failed for inputs\n")
         appendInputs(context, inputs)
         appendLine()
         val cause = stacktraces.root(e)
         when (val stack = stacktraces.throwableLocation(cause, 4)) {
            null -> appendLine("Caused by $e")
            else -> {
               appendLine("Caused by $e at")
               stack.forEach { appendLine("\t$it") }
            }
         }
         appendLine()
      }
   )
}

private fun printSkippedMessage(
   context: PropertyContext,
   inputs: List<Any?>
) {
   println(
      buildString {
         appendLine("Property test skipped for inputs\n")
         appendInputs(context, inputs)
      }
   )
}

private fun StringBuilder.appendInputs(context: PropertyContext, inputs: List<Any?>) {
   iterator {
      inputs.forEach { input ->
         yield(input.print().value)
      }
      context.generatedSamples().forEach { sample ->
         yield("${sample.value.print().value} (generated within property context)")
      }
   }.withIndex().forEach { (index, input) ->
      appendLine("$index) $input")
   }
}

private fun buildMaxFailureErrorMessage(
   context: PropertyContext,
   config: PropTestConfig,
   inputs: List<Any?>,
): String {
   return buildString {
      appendLine("Property failed ${context.failures()} times (maxFailure rate was ${config.maxFailure})")
      appendLine("Last error was caused by args:")
      inputs.withIndex().forEach { (index, value) ->
         appendLine("  $index) ${value.print().value}")
      }
   }
}
