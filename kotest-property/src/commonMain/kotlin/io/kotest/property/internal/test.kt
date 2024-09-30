package io.kotest.property.internal

import io.kotest.assertions.print.print
import io.kotest.mpp.stacktraces
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.AssumptionFailedException
import io.kotest.property.BeforePropertyContextElement
import io.kotest.property.Classifier
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.seed.writeFailedSeed
import io.kotest.property.statistics.outputStatistics
import kotlin.coroutines.coroutineContext

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
               if (label != null) context.classify(k, label)
            }
         }

         coroutineContext[BeforePropertyContextElement]?.before?.invoke()
         testFn()
         context.markSuccess()
         coroutineContext[AfterPropertyContextElement]?.after?.invoke()
      } catch (e: AssumptionFailedException) {
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
   if (config.maxFailure == 0) {
      printFailureMessage(context, inputs, e)
      throwPropertyTestAssertionError(shrinkfn(), e, context.attempts(), seed)
   } else if (context.failures() > config.maxFailure) {
      val error = buildMaxFailureErrorMessage(context, config, inputs)
      throwPropertyTestAssertionError(shrinkfn(), AssertionError(error), context.attempts(), seed)
   }
}

private fun printFailureMessage(
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
