package io.kotest.property

import io.kotest.property.shrinker.shrink
import kotlin.math.min
import kotlin.random.Random

inline fun <reified A, reified B> forAll(
   iterations: Int = 1000,
   seed: Long = 0,
   minSuccess: Int = Int.MAX_VALUE,
   maxFailure: Int = 0,
   shrinking: ShrinkingMode = ShrinkingMode.Bounded(1000),
   noinline property: (A, B) -> Boolean
) = forAll(Arbitrary.default(iterations), Arbitrary.default(iterations), seed, minSuccess, maxFailure, shrinking, property)

fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   seed: Long = 0,
   minSuccess: Int = Int.MAX_VALUE,
   maxFailure: Int = 0,
   shrinking: ShrinkingMode = ShrinkingMode.Bounded(1000),
   property: (A, B) -> Boolean
) {

   val random = if (seed == 0L) Random.Default else Random(seed)
   val context = PropertyContext()

   genA.generate(random).map { a ->
      genB.generate(random).map { b ->
         when (property(a.value, b.value)) {
            true -> context.success()
            false -> context.failure()
         }
         if (context.failures() > maxFailure) {
            val smallestA = shrink(a, { a2 -> property(a2, b.value) }, shrinking)
            val smallestB = shrink(b, { b2 -> property(a.value, b2) }, shrinking)
            val inputs = listOf(
               PropertyFailureInput(a.value, smallestA),
               PropertyFailureInput(b.value, smallestB)
            )
            throw propertyAssertionError(
               AssertionError("Property Test has failed ${context.failures()} times (max failure rate was $maxFailure)"),
               context.attempts(),
               inputs
            )
         }
      }
   }

   val min = min(minSuccess, context.attempts())
   if (context.successes() < min) {
      throw AssertionError("Property test has passed ${context.successes()} times (min success rate was $min)")
   }
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
