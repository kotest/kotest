package io.kotest.property.internal

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext

/**
 * Performs a property test for a single set of values, tracking the min success and max failure rates.
 * Will perform shrinking and throw when the property test is deemed to have failed.
 */
internal suspend fun test(
   context: PropertyContext,
   config: PropTestConfig,
   shrinkfn: ShrinkFn,
   inputs: List<Any?>,
   fn: suspend () -> Any
) {
   try {
      fn()
      context.markSuccess()
   } catch (e: AssertionError) { // we track assertion errors and try to shrink them
      context.markFailure()
      handleException(context, shrinkfn, inputs, e, config)
   } catch (e: Exception) {
      context.markFailure()
      when (e::class.simpleName) {
         "AssertionError",
         "AssertionFailedError",
         "ComparisonFailure" -> handleException(context, shrinkfn, inputs, e, config)
         // any other non assertion error exception is an immediate fail without shrink
         else -> throwPropertyTestAssertionError(e, context.attempts())
      }
   }
}

internal suspend fun handleException(
   context: PropertyContext,
   shrinkfn: ShrinkFn,
   inputs: List<Any?>,
   e: Throwable,
   config: PropTestConfig
) {
   if (config.maxFailure == 0) {
      throwPropertyTestAssertionError(inputs, shrinkfn(), e, context.attempts())
   } else if (context.failures() > config.maxFailure) {
      val t = AssertionError("Property failed ${context.failures()} times (maxFailure rate was ${config.maxFailure})")
      throwPropertyTestAssertionError(inputs, shrinkfn(), t, context.attempts())
   }
}
