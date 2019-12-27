package io.kotest.property

import io.kotest.property.shrinker.shrink
import kotlin.math.min
import kotlin.random.Random

data class PropTestArgs(
   val seed: Long = 0,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinking: ShrinkingMode = ShrinkingMode.Bounded(1000)
)

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

   val random = when (args.seed) {
      0L -> Random.Default
      else -> Random(args.seed)
   }
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

fun main() {

   // tests 1000 random ints with all integers 0 to 100
   forAll(
      Arbitrary.int(1000),
      Progression.int(0..100)
   ) { a, b ->
      a + b == b + a
   }

   // tests 10 random longs in the range 11 to MaxLong with all combinations of a-z strings from 0 to 10 characters
   forAll(
      Arbitrary.long(10, 11..Long.MAX_VALUE),
      Progression.azstring(0..10)
   ) { a, b ->
      b.length < a
   }

   // convenience functions which mimics the existing style
   // tests random longs, using reflection to pick up the Arbitrary instances
   // each arb will have the same number of iterations
   forAll<Long, Long>(1000) { a, b -> a + b == b + a }
}
