package io.kotest.property.statistics

import io.kotest.assertions.fail
import io.kotest.common.ExperimentalKotest
import io.kotest.property.PropertyContext

/**
 * Asserts that the given [classification] was applied to at least [percentage] number of tests.
 *
 * For example, to check that at least 25% of the iterations where classified as 'even':
 *
 *       withCoveragePercentage("even", 25.0) {
 *          forAll(Arb.int()) { a ->
 *             classify(a % 2 == 0, "even")
 *             a + a == 2 * a
 *          }
 *       }
 *
 */
@ExperimentalKotest
suspend fun withCoveragePercentage(
   classification: Any?,
   percentage: Double,
   f: suspend () -> PropertyContext
): PropertyContext {
   val context = f()
   val stats = context.statistics()[null] ?: emptyMap()
   val count = stats[classification] ?: 0
   val attempts = context.attempts()
   val actual = (count.toDouble() / attempts.toDouble()) * 100.0
   if (actual < percentage)
      fail("Required coverage of $percentage% for [${classification}] but was [${actual.toInt()}%]")
   return context
}


/**
 * Asserts that the given [classification] was applied to at least [count] number of tests.
 *
 * For example, to check that at least 150 of the iterations where classified as 'even':
 *
 *       withCoverageCount("even", 150) {
 *          forAll(Arb.int()) { a ->
 *             classify(a % 2 == 0, "even")
 *             a + a == 2 * a
 *          }
 *       }
 *
 */
@ExperimentalKotest
suspend fun withCoverageCount(
   classification: Any?,
   count: Int,
   f: suspend () -> PropertyContext
): PropertyContext {
   val context = f()
   val stats = context.statistics()[null] ?: emptyMap()
   val actual = stats[classification] ?: 0
   if (actual < count)
      fail("Required coverage of $count for [${classification}] but was [${actual}]")
   return context
}
