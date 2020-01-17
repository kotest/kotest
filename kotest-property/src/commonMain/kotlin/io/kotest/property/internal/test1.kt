@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.property.*
import io.kotest.property.arbitrary.PropertyInput

suspend fun <A> test1(
   genA: Gen<A>,
   args: PropTestArgs,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = args.seed.random()

   with(context) {
      genA.generate(random).forEach { a ->
         try {
            property(a.value)
            context.markSuccess()
         } catch (e: AssertionError) {
            context.markFailure()
            if (args.maxFailure == 0) {
               fail(a, shrink(a, property, args), e, attempts())
            } else if (failures() > args.maxFailure) {
               val t = AssertionError("Property failed ${failures()} times (maxFailure rate was ${args.maxFailure})")
               fail(a, shrink(a, property, args), t, attempts())
            }
         }
      }
      context.checkMaxSuccess(args)
   }

   return context
}

// shrinks a single set of failed inputs returning a tuple of the smallest values
suspend fun <A> shrink(
   a: PropertyInput<A>,
   property: suspend PropertyContext.(A) -> Unit,
   args: PropTestArgs
): A {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   return with(context) {
      shrink(a, { property(it) }, args.shrinking)
   }
}

// creates an exception for failed, shrunk, values and throws
fun <A> fail(
   a: PropertyInput<A>,
   shrink: A,
   e: Error, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(PropertyFailureInput(a.value, shrink))
   throw propertyAssertionError(e, attempts, inputs)
}
