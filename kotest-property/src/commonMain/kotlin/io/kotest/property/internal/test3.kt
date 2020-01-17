@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.fp.Tuple3
import io.kotest.property.*
import io.kotest.property.arbitrary.PropertyInput

suspend fun <A, B, C> test3(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   args: PropTestArgs,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = args.seed.random()

   with(context) {
      genA.generate(random).forEach { a ->
         genB.generate(random).forEach { b ->
            genC.generate(random).forEach { c ->
               try {
                  property(a.value, b.value, c.value)
                  context.markSuccess()
               } catch (e: AssertionError) {
                  context.markFailure()
                  if (args.maxFailure == 0) {
                     fail(
                        a,
                        b,
                        c,
                        shrink(a, b, c, property, args),
                        e,
                        attempts()
                     )
                  } else if (failures() > args.maxFailure) {
                     val t =
                        AssertionError("Property failed ${failures()} times (maxFailure rate was ${args.maxFailure})")
                     fail(
                        a,
                        b,
                        c,
                        shrink(a, b, c, property, args),
                        t,
                        attempts()
                     )
                  }
               }
            }
         }
      }
      context.checkMaxSuccess(args)
   }

   return context
}

// shrinks a single set of failed inputs returning a tuple of the smallest values
suspend fun <A, B, C> shrink(
   a: PropertyInput<A>,
   b: PropertyInput<B>,
   c: PropertyInput<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit,
   args: PropTestArgs
): Tuple3<A, B, C> {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   return with(context) {
      val smallestA =
         shrink(a, { property(it, b.value, c.value) }, args.shrinking)
      val smallestB =
         shrink(b, { property(a.value, it, c.value) }, args.shrinking)
      val smallestC =
         shrink(c, { property(a.value, b.value, it) }, args.shrinking)
      Tuple3(smallestA, smallestB, smallestC)
   }
}

// creates an exception for failed, shrunk, values and throws
fun <A, B, C> fail(
   a: PropertyInput<A>,
   b: PropertyInput<B>,
   c: PropertyInput<C>,
   shrink: Tuple3<A, B, C>,
   e: AssertionError, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(a.value, shrink.a),
      PropertyFailureInput(b.value, shrink.b),
      PropertyFailureInput(c.value, shrink.c)
   )
   throw propertyAssertionError(e, attempts, inputs)
}
