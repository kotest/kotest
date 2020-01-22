@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.fp.Tuple2
import io.kotest.property.*

suspend fun <A, B> test2(
   argA: Argument<A>,
   argB: Argument<B>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   argA.values(random).forEach { a ->
      argB.values(random).forEach { b ->
         runTest(context,
            { property(a.value, b.value) },
            { handleFailureAndShrink(context, a, b, it, config, property) }
         )
      }
   }
   context.checkMaxSuccess(config)
   return context
}

suspend fun <A, B> handleFailureAndShrink(
   context: PropertyContext,
   a: ArgumentValue<A>,
   b: ArgumentValue<B>,
   t: Throwable,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B) -> Unit
) {
   context.markFailure()
   val error = context.checkMaxFailures(config.maxFailure, t)
   if (error != null)
      fail(Tuple2(a, b), shrink(a, b, property), error, context.attempts())
}
