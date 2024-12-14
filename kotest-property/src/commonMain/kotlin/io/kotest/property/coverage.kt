package io.kotest.property

import io.kotest.assertions.fail

/**
 * Asserts that the given [label] was applied to at least [percentage] number of tests.
 * See [PropertyContext.classify].
 *
 * Example, checks that for at least 25% of the iterations a was classified as "even".
 *
 *       checkCoverage("even", 25.0) {
 *          forAll(Arb.int()) { a ->
 *             classify(a % 2 == 0, "even")
 *             a + a == 2 * a
 *          }
 *       }
 *
 */
@Suppress("DEPRECATION")
@Deprecated("Use labels. See https://kotest.io/docs/proptest/property-test-statistics.html")
suspend fun checkCoverage(label: String, percentage: Double, f: suspend () -> PropertyContext): PropertyContext {
   val context = f()
   val labelled = context.classifications()[label] ?: 0
   val attempts = context.attempts()
   val actual = (labelled.toDouble() / attempts.toDouble()) * 100.0
   if (actual < percentage)
      fail("Property test required coverage of $percentage% for [$label] but was [${actual.toInt()}%]")
   return context
}

/**
 * Asserts more than one label coverage at once.
 *
 *  Example, checks that we had at least 25% "even" and 25% "odd" iterations.
 *
 *       checkCoverage("even" to 25.0, "odd" to 25.0) {
 *          forAll(Arb.int()) { a ->
 *             classify(a % 2 == 0, "even", "odd")
 *             a + a == 2 * a
 *          }
 *       }
 */
@Suppress("DEPRECATION")
@Deprecated("Use labels. See https://kotest.io/docs/proptest/property-test-statistics.html")
suspend fun checkCoverage(vararg pairs: Pair<String, Double>, f: suspend () -> PropertyContext): PropertyContext {
   val context = f()
   val attempts = context.attempts()
   val classifications = context.classifications()
   pairs.forEach { (label, required) ->
      val actualCount = classifications[label] ?: 0
      val actual = (actualCount.toDouble() / attempts.toDouble()) * 100.0
      if (actual < required)
         fail("Property test required coverage of $required% for [$label] but was [${actual.toInt()}%]")
   }
   return context
}
