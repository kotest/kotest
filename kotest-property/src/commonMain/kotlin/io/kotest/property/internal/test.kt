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
import io.kotest.property.seed.cleanUpSeedFiles
import io.kotest.property.seed.clearFailedSeed
import io.kotest.property.seed.writeFailedSeedIfEnabled
import io.kotest.property.statistics.outputStatistics
import kotlin.coroutines.coroutineContext

/**
 * Performs a property test for a single set of values, tracking the min success and max failure rates.
 * Will perform shrinking and throw when the property test is deemed to have failed.
 * If a classifier is provided, will classify each value.
 *
 * If registered, will invoke beforeProperty and afterProperty lifecycle methods.
 */
internal suspend fun test(
   context: PropertyContext,
   config: PropTestConfig,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   classifiers: List<Classifier<out Any?>?>,
   seed: Long,
   contextualSeed: Long,
   testFn: suspend () -> Any
) {
   require(inputs.size == classifiers.size)
   context.markEvaluation()
   context.setupContextual(RandomSource.seeded(contextualSeed))
   try {

      inputs.indices.forEach { k ->
         val value = inputs[k]
         val classifier = classifiers[k]
         if (classifier != null) {
            val label = (classifier as Classifier<Any?>).classify(value)
            if (label != null) context.classify(k, label)
         }
      }

      coroutineContext[BeforePropertyContextElement]?.before?.invoke()
      testFn()
      context.markSuccess()
      coroutineContext[AfterPropertyContextElement]?.after?.invoke()
      clearFailedSeed()
   } catch (e: AssumptionFailedException) {
      // we don't mark failed assumptions as errors
   } catch (e: Throwable) {
      // we track any throwables and try to shrink them
      context.markFailure()
      cleanUpSeedFiles()
      outputStatistics(context, inputs.size, false)
      handleException(context, shrinkfn, inputs, seed, e, config)
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
   if (config.maxFailure == 0) {
      printFailureMessage(context, inputs, e)
      writeFailedSeedIfEnabled(seed)
      throwPropertyTestAssertionError(shrinkfn(), e, context.attempts(), seed)
   } else if (context.failures() > config.maxFailure) {
      var error = "Property failed ${context.failures()} times (maxFailure rate was ${config.maxFailure})\n"
      error += "Last error was caused by args:\n"
      inputs.withIndex().forEach { (index, value) ->
         error += "  $index) ${value.print().value}\n"
      }
      writeFailedSeedIfEnabled(seed)
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
         appendLine("Property test failed for inputs")
         appendLine()

         var inputIndex = 0
         inputs.forEach { input ->
            appendLine("${inputIndex++}) ${input.print().value}")
         }
         context.generatedSamples().forEach { sample ->
            val printed = "${sample.value.print().value} (generated within property context)"
            appendLine("${inputIndex++}) $printed")
         }
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
