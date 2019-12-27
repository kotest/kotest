package io.kotest.property

import io.kotest.property.shrinker.shrink
import kotlin.math.min
import kotlin.random.Random

fun Long.random() = when (this) {
   0L -> Random.Default
   else -> Random(this)
}

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
         true -> context.success()
         false -> context.failure()
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

inline fun <reified A, reified B> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   noinline property: (A, B) -> Boolean
) = forAll(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   args,
   property
)

fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   args: PropTestArgs = PropTestArgs(),
   property: (A, B) -> Boolean
): PropertyContext {

   val random = args.seed.random()
   val context = PropertyContext()

   genA.generate(random).forEach { a ->
      genB.generate(random).forEach { b ->
         when (property(a.value, b.value)) {
            true -> context.success()
            false -> context.failure()
         }
         if (context.failures() > args.maxFailure) {
            val smallestA = shrink(a, { a2 -> property(a2, b.value) }, args.shrinking)
            val smallestB = shrink(b, { b2 -> property(a.value, b2) }, args.shrinking)
            val inputs = listOf(
               PropertyFailureInput(a.value, smallestA),
               PropertyFailureInput(b.value, smallestB)
            )
            throw propertyAssertionError(
               AssertionError("Prop test failed ${context.failures()} times (max failure rate was ${args.maxFailure})"),
               context.attempts(),
               inputs
            )
         }
      }
   }

   val min = min(args.minSuccess, context.attempts())
   if (context.successes() < min) {
      throw AssertionError("Prop test passed ${context.successes()} times (min success rate was $min)")
   }

   return context
}
