@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.property.*

suspend fun <A> test1(
   argA: Argument<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   argA.values(random).forEach { a ->
      runTest(context,
         { property(a.value) },
         { handleFailureAndShrink(context, a, it, config, property) }
      )
   }
   context.checkMaxSuccess(config)
   return context
}

suspend fun <A> handleFailureAndShrink(
   context: PropertyContext,
   a: ArgumentValue<A>,
   t: Throwable,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
) {
   context.markFailure()
   val error = context.checkMaxFailures(config.maxFailure, t)
   if (error != null)
      fail(a, shrink(a, property), error, context.attempts())
}
