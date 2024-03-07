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
): PropertyContext = withCoveragePercentages(mapOf(classification to percentage), f)

@ExperimentalKotest
suspend fun withCoveragePercentages(
   classifications: Map<Any?, Double>,
   f: suspend () -> PropertyContext
): PropertyContext {
   val context = f()
   val stats = context.statistics()[null] ?: emptyMap()
   classifications.forEach { (classification, min) ->
      val count = stats[classification] ?: 0
      val attempts = context.attempts()
      val actual = (count.toDouble() / attempts.toDouble()) * 100.0
      if (actual < min)
         fail("Required coverage of $min% for [${classification}] but was [${actual.toInt()}%]")
   }
   return context
}

/**
 * Asserts that the given [classification] was applied to at least [count] number of tests.
 *
 * For example, to check that at least 150 of the iterations were classified as 'even':
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
): PropertyContext = withCoverageCounts(mapOf(classification to count), f)

/**
 * Asserts that the given classifications were statisfied.
 *
 * For example, to check that at least 150 of the iterations were classified as 'even', and 200 were 'positive':
 *
 *       withCoverageCounts("even", 150, "positive", 200) {
 *          forAll(Arb.int()) { a ->
 *             classify(a % 2 == 0, "even")
 *             a + a == 2 * a
 *          }
 *       }
 *
 */
@ExperimentalKotest
suspend fun withCoverageCounts(
   classifications: Map<Any?, Int>,
   f: suspend () -> PropertyContext
): PropertyContext {
   val context = f()
   val stats = context.statistics()[null] ?: emptyMap()
   classifications.forEach { (classification, min) ->
      val actual = stats[classification] ?: 0
      if (actual < min)
         fail("Required coverage of $min for [${classification}] but was [${actual}]")
   }
   return context
}
