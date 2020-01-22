@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.fp.Tuple3
import io.kotest.property.*

suspend fun <A, B, C> test3(
   argA: Argument<A>,
   argB: Argument<B>,
   argC: Argument<C>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   argA.values(random).forEach { a ->
      argB.values(random).forEach { b ->
         argC.values(random).forEach { c ->
            runTest(context,
               { property(a.value, b.value, c.value) },
               { handleFailureAndShrink(context, a, b, c, it, config, property) }
            )
         }
      }
   }
   context.checkMaxSuccess(config)
   return context
}

suspend fun <A, B, C> handleFailureAndShrink(
   context: PropertyContext,
   a: ArgumentValue<A>,
   b: ArgumentValue<B>,
   c: ArgumentValue<C>,
   t: Throwable,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C) -> Unit
) {
   context.markFailure()
   val error = context.checkMaxFailures(config.maxFailure, t)
   if (error != null)
      fail(Tuple3(a, b, c), shrink(a, b, c, property), error, context.attempts())
}
