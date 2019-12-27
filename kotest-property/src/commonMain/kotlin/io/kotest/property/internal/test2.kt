@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property.internal

import io.kotest.tuples.Tuple2
import io.kotest.property.*
import io.kotest.property.arbitrary.PropertyInput
import kotlin.math.min

inline fun <A, B> test2(
   genA: Gen<A>,
   genB: Gen<B>,
   args: PropTestArgs,
   property: PropertyContext.(A, B) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = args.seed.random()

   with(context) {
      genA.generate(random).forEach { a ->
         genB.generate(random).forEach { b ->
            try {
               property(a.value, b.value)
               context.markSuccess()
               // we track assertion errors and try to shrink them
            } catch (e: AssertionError) {
               handleException(a, b, e, args, property)
               // any other non assertion error exception is an immediate fail without shrink
            } catch (e: Exception) {
               if (e::class.simpleName == "AssertionError"
                  || e::class.simpleName == "AssertionFailedError"
                  || e::class.simpleName == "ComparisonFailure"
               ) {
                  handleException(a, b, e, args, property)
               } else {
                  context.markFailure()
                  fail(e, attempts())
               }
            }
         }
      }
      context.checkMaxSuccess(args)
   }

   return context
}

inline fun <A, B> PropertyContext.handleException(
   a: PropertyInput<A>,
   b: PropertyInput<B>,
   e: Throwable,
   args: PropTestArgs,
   property: PropertyContext.(A, B) -> Unit
) {
   markFailure()
   if (args.maxFailure == 0) {
      fail(a, b, shrink(a, b, property, args), e, attempts())
   } else if (failures() > args.maxFailure) {
      val t = AssertionError("Property failed ${failures()} times (maxFailure rate was ${args.maxFailure})")
      fail(a, b, shrink(a, b, property, args), t, attempts())
   }
}

fun PropertyContext.checkMaxSuccess(args: PropTestArgs) {
   val min = min(args.minSuccess, attempts())
   if (successes() < min) {
      val e = AssertionError("Property passed ${successes()} times (minSuccess rate was $min)")
      fail(e, attempts())
   }
}

// shrinks a single set of failed inputs returning a tuple of the smallest values
inline fun <A, B> shrink(
   a: PropertyInput<A>,
   b: PropertyInput<B>,
   property: PropertyContext.(A, B) -> Unit,
   args: PropTestArgs
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
fun <A, B> fail(
   a: PropertyInput<A>,
   b: PropertyInput<B>,
   shrink: Tuple2<A, B>,
   e: Throwable, // the underlying failure reason,
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
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   throw propertyAssertionError(e, attempts, emptyList())
}
