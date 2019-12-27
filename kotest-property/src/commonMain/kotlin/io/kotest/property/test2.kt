package io.kotest.property

import io.kotest.Tuple2
import kotlin.math.min

fun <A, B> test2(
   genA: Gen<A>,
   genB: Gen<B>,
   args: PropTestArgs,
   property: PropertyContext.(A, B) -> Unit
): PropertyContext {

   // shrinks a single set of failed inputs returning a tuple of the smallest values
   fun shrink(
      a: PropertyInput<A>,
      b: PropertyInput<B>
   ): Tuple2<A, B> {
      // we use a new context for the shrinks, as we don't want to affect classification etc
      val context = PropertyContext()
      return with(context) {
         val smallestA = shrink(a, { property(it, b.value) }, args.shrinking)
         val smallestB = shrink(b, { property(a.value, it) }, args.shrinking)
         Tuple2(smallestA, smallestB)
      }
   }

   // creates an exception for failed, shrunk, values and throws
   fun fail(
      a: PropertyInput<A>,
      b: PropertyInput<B>,
      shrink: Tuple2<A, B>,
      e: AssertionError, // the underlying failure reason,
      attempts: Int
   ) {
      val inputs = listOf(
         PropertyFailureInput(a.value, shrink.a),
         PropertyFailureInput(b.value, shrink.b)
      )
      throw propertyAssertionError(e, attempts, inputs)
   }

   // creates an exception without specifying parameter details and throws
   fun fail(
      e: AssertionError, // the underlying failure reason,
      attempts: Int
   ) {
      throw propertyAssertionError(e, attempts, emptyList())
   }

   val context = PropertyContext()
   val random = args.seed.random()

   with(context) {
      genA.generate(random).forEach { a ->
         genB.generate(random).forEach { b ->
            try {
               property(a.value, b.value)
               context.markSuccess()
            } catch (e: AssertionError) {
               context.markFailure()
               if (args.maxFailure == 0) {
                  fail(a, b, shrink(a, b), e, attempts())
               } else if (failures() > args.maxFailure) {
                  val t = AssertionError("Property failed ${failures()} times (maxFailure rate was ${args.maxFailure})")
                  fail(a, b, shrink(a, b), t, attempts())
               }
            }
         }
      }
      val min = min(args.minSuccess, attempts())
      if (context.successes() < min) {
         val e = AssertionError("Property passed ${successes()} times (minSuccess rate was $min)")
         fail(e, attempts())
      }
   }

   return context
}
