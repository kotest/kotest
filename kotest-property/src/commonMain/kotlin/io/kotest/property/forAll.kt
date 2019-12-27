package io.kotest.property

import kotlin.math.min

inline fun <reified A> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   noinline property: (A) -> Boolean
) = forAll(
   Arbitrary.default(iterations),
   args,
   property
)

fun <A> forAll(
   genA: Gen<A>,
   args: PropTestArgs = PropTestArgs(),
   property: (A) -> Boolean
): PropertyContext {

   val random = args.seed.random()
   val context = PropertyContext()

   genA.generate(random).forEach { a ->
      when (property(a.value)) {
         true -> context.markSuccess()
         false -> context.markFailure()
      }
      if (context.failures() > args.maxFailure) {
         val smallestA = shrink(a, { property(it) }, args.shrinking)
         val inputs = listOf(
            PropertyFailureInput(a.value, smallestA)
         )
         throw propertyAssertionError(
            AssertionError("Prop test failed ${context.failures()} times (max failure rate was ${args.maxFailure})"),
            context.attempts(),
            inputs
         )
      }
   }

   val min = min(args.minSuccess, context.attempts())
   if (context.successes() < min) {
      throw AssertionError("Prop test passed ${context.successes()} times (min success rate was $min)")
   }

   return context
}
