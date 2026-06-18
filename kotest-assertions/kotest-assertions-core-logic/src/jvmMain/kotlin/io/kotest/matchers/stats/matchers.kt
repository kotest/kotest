package io.kotest.matchers.stats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.sqrt

/**
 * Asserts that mean of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveMean]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 1, 3)
 * val firstMean   = BigDecimal("1.66667")
 * val secondMean = BigDecimal("1.6667")
 *
 * collection.shouldHaveMean(firstMean, 5)      // Assertion passes
 * collection.shouldHaveMean(secondMean)       // Assertion passes
 *
 * collection.shouldHaveMean(firstMean)         // Assertion fails
 * collection.shouldHaveMean(secondMean, 5)    // Assertion fails
 * ```
 *
 * @param value - expected mean value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveMean(value: BigDecimal, precision: Int = 4) = this should haveMean<T>(value, precision)

/**
 * Asserts that mean of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveMean]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 1, 3)
 * val firstMean   = 1.66667
 * val secondMean = 1.6667
 *
 * collection.shouldHaveMean(firstMean, 5)      // Assertion passes
 * collection.shouldHaveMean(secondMean)       // Assertion passes
 *
 * collection.shouldHaveMean(firstMean)         // Assertion fails
 * collection.shouldHaveMean(secondMean, 5)    // Assertion fails
 * ```
 *
 * @param value - expected mean value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveMean(value: Double, precision: Int = 4) = this should haveMean<T>(value, precision)

/**
 * Asserts that mean of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveMean]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 1, 3)
 * val firstMean   = BigDecimal("2.0")
 * val secondMean = BigDecimal("1.6666667")
 *
 * collection.shouldNotHaveMean(firstMean)                // Assertion passes
 * collection.shouldNotHaveMean(secondMean, 5)           // Assertion passes
 *
 * collection.shouldNotHaveMean(BigDecimal("1.6667"))    // Assertion fails
 * collection.shouldNotHaveMean(BigDecimal("1.6667"), 4) // Assertion fails
 * ```
 *
 * @param value - not expected mean value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveMean(value: BigDecimal, precision: Int = 4) = this shouldNot haveMean<T>(value, precision)

/**
 * Asserts that mean of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveMean]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 1, 3)
 *
 * collection.shouldNotHaveMean(2.0)          // Assertion passes
 * collection.shouldNotHaveMean(1.67, 5)      // Assertion passes
 *
 * collection.shouldNotHaveMean(1.6667)       // Assertion fails
 * collection.shouldNotHaveMean(1.6667, 4)    // Assertion fails
 * ```
 *
 * @param value - not expected mean value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveMean(value: Double, precision: Int = 4) = this shouldNot haveMean<T>(value, precision)

/**
 * Asserts that variance of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveVariance]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 * val firstVariance   = BigDecimal("0.66667")
 * val secondVariance = BigDecimal("0.6667")
 *
 * collection.shouldHaveVariance(firstVariance, 5)      // Assertion passes
 * collection.shouldHaveVariance(secondVariance)       // Assertion passes
 *
 * collection.shouldHaveVariance(firstVariance)         // Assertion fails
 * collection.shouldHaveVariance(secondVariance, 5)    // Assertion fails
 * ```
 *
 * @param value - expected variance value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveVariance(value: BigDecimal, precision: Int = 4) = this should haveVariance<T>(value, precision)

/**
 * Asserts that variance of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveVariance]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldHaveVariance(0.66667, 5)      // Assertion passes
 * collection.shouldHaveVariance(0.6667)          // Assertion passes
 *
 * collection.shouldHaveVariance(0.67)         // Assertion fails
 * collection.shouldHaveVariance(0.6667, 5)    // Assertion fails
 * ```
 *
 * @param value - expected variance value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveVariance(value: Double, precision: Int = 4) = this should haveVariance<T>(value, precision)

/**
 * Asserts that variance of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveVariance]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldNotHaveVariance(BigDecimal("1.01"), 5)        // Assertion passes
 * collection.shouldNotHaveVariance(BigDecimal("0.666667"))       // Assertion passes
 *
 * collection.shouldNotHaveVariance(BigDecimal("0.6667"))         // Assertion fails
 * collection.shouldNotHaveVariance(BigDecimal("0.66667"), 5)     // Assertion fails
 * ```
 *
 * @param value - not expected variance value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveVariance(value: BigDecimal, precision: Int = 4) = this shouldNot haveVariance<T>(value, precision)

/**
 * Asserts that variance of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveVariance]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldNotHaveVariance(1.01, 5)        // Assertion passes
 * collection.shouldNotHaveVariance(0.666667)       // Assertion passes
 *
 * collection.shouldNotHaveVariance(0.6667)         // Assertion fails
 * collection.shouldNotHaveVariance(0.66667, 5)     // Assertion fails
 * ```
 *
 * @param value - not expected variance value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveVariance(value: Double, precision: Int = 4) = this shouldNot haveVariance<T>(value, precision)

/**
 * Asserts that standard deviation of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveStandardDeviation]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldHaveStandardDeviation(BigDecimal("0.82"), 2)      // Assertion passes
 * collection.shouldHaveStandardDeviation(BigDecimal("0.8165"))       // Assertion passes
 *
 * collection.shouldHaveStandardDeviation(BigDecimal("0.82"))         // Assertion fails
 * collection.shouldHaveStandardDeviation(BigDecimal("0.8165"), 5)    // Assertion fails
 * ```
 *
 * @param value - expected standard deviation value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveStandardDeviation(value: BigDecimal, precision: Int = 4) = this should haveStandardDeviation<T>(value, precision)

/**
 * Asserts that standard deviation of the Collection elements equals to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldNotHaveStandardDeviation]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldHaveStandardDeviation(0.82, 2)      // Assertion passes
 * collection.shouldHaveStandardDeviation(0.8165)       // Assertion passes
 *
 * collection.shouldHaveStandardDeviation(0.82)         // Assertion fails
 * collection.shouldHaveStandardDeviation(0.8165, 5)    // Assertion fails
 * ```
 *
 * @param value - expected standard deviation value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldHaveStandardDeviation(value: Double, precision: Int = 4) = this should haveStandardDeviation<T>(value, precision)

/**
 * Asserts that standard deviation of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveStandardDeviation]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldNotHaveStandardDeviation(BigDecimal("0.8165"), 5)    // Assertion passes
 * collection.shouldNotHaveStandardDeviation(BigDecimal("0.8333"))       // Assertion passes
 *
 * collection.shouldNotHaveStandardDeviation(BigDecimal("0.8165"))       // Assertion fails
 * collection.shouldNotHaveStandardDeviation(BigDecimal("0.82"), 2)      // Assertion fails
 * ```
 *
 * @param value - not expected standard deviation value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveStandardDeviation(value: BigDecimal, precision: Int = 4) = this shouldNot haveStandardDeviation<T>(value, precision)

/**
 * Asserts that standard deviation of the Collection elements doesn't equal to [value] with default
 * or specific [precision]. Default precision equals 4 digits after decimal point.
 *
 * Opposite of [shouldHaveStandardDeviation]
 *
 * Example:
 * ```
 *
 * val collection = listOf(1, 2, 3)
 *
 * collection.shouldNotHaveStandardDeviation(0.8165, 5)    // Assertion passes
 * collection.shouldNotHaveStandardDeviation(0.8333)       // Assertion passes
 *
 * collection.shouldNotHaveStandardDeviation(0.8165)       // Assertion fails
 * collection.shouldNotHaveStandardDeviation(0.82, 2)      // Assertion fails
 * ```
 *
 * @param value - not expected standard deviation value
 * @param precision - precision, by default - 4 digits after decimal point
 *
 */
fun <T : Number> Collection<T>.shouldNotHaveStandardDeviation(value: Double, precision: Int = 4) = this shouldNot haveStandardDeviation<T>(value, precision)

private val defaultMathContext = MathContext(64, RoundingMode.HALF_UP)
private fun BigDecimal.round(precision: Int): BigDecimal = this.setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()

private fun <T : Number> calculateMean(collection: Collection<T>): BigDecimal {
   var sum: BigDecimal = BigDecimal.ZERO
   for (elem in collection) {
      sum += BigDecimal(elem.toString())
   }
   return sum.divide(BigDecimal(collection.size), defaultMathContext)
}

private fun <T : Number> calculateVariance(collection: Collection<T>): BigDecimal {
   val mean: BigDecimal = calculateMean(collection)
   var sumOfSquaredDifferences: BigDecimal = BigDecimal.ZERO
   for (elem in collection) {
      sumOfSquaredDifferences += (BigDecimal(elem.toString()) - mean).pow(2)
   }
   return sumOfSquaredDifferences.divide(BigDecimal(collection.size), defaultMathContext)
}

private fun <T : Number> calculateStandardDeviation(collection: Collection<T>): BigDecimal {
   val variance = calculateVariance(collection)
   val two = BigDecimal(2)
   var x0 = BigDecimal.ZERO
   var x1 = BigDecimal(sqrt(variance.toDouble()))
   while (x0 != x1) {
      x0 = x1
      x1 = variance.divide(x0, defaultMathContext)
      x1 = x1.add(x0)
      x1 = x1.divide(two, defaultMathContext)
   }
   return x1
}

private fun <T : Number> testMean(collection: Collection<T>, expectedValue: BigDecimal, precision: Int): MatcherResult {
   val expected = expectedValue.stripTrailingZeros()
   val actual = if (collection.isEmpty()) BigDecimal.ZERO else calculateMean(collection).round(precision)
   return MatcherResult(
      expected.compareTo(actual) == 0,
      { "Collection should have mean $expected but was $actual" },
      {
         "Collection should not have mean $expected but was $actual"
      })
}

private fun <T : Number> testVariance(
   collection: Collection<T>,
   expectedValue: BigDecimal,
   precision: Int
): MatcherResult {
   val expected = expectedValue.stripTrailingZeros()
   val actual = if (collection.isEmpty()) BigDecimal.ZERO else calculateVariance(collection).round(precision)
   return MatcherResult(
      expected.compareTo(actual) == 0,
      { "Collection should have variance $expected but was $actual" },
      {
         "Collection should not have variance $expected but was $actual"
      })
}

private fun <T : Number> testStandardDeviation(
   collection: Collection<T>,
   expectedValue: BigDecimal,
   precision: Int
): MatcherResult {
   val expected = expectedValue.stripTrailingZeros()
   val actual = if (collection.isEmpty()) BigDecimal.ZERO else calculateStandardDeviation(collection).round(precision)
   return MatcherResult(
      expected.compareTo(actual) == 0,
      { "Collection should have standard deviation $expected but was $actual" },
      {
         "Collection should not have standard deviation $expected but was $actual"
      })
}

fun <T : Number> haveMean(expectedValue: BigDecimal, precision: Int = 4) = object :
  Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testMean(value, expectedValue, precision)
}

fun <T : Number> haveMean(expectedValue: Double, precision: Int = 4) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testMean(value, expectedValue.toBigDecimal(), precision)
}

fun <T : Number> haveVariance(expectedValue: BigDecimal, precision: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testVariance(value, expectedValue, precision)
}

fun <T : Number> haveVariance(expectedValue: Double, precision: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testVariance(value, expectedValue.toBigDecimal(), precision)
}

fun <T : Number> haveStandardDeviation(expectedValue: BigDecimal, precision: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testStandardDeviation(value, expectedValue, precision)
}

fun <T : Number> haveStandardDeviation(expectedValue: Double, precision: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testStandardDeviation(value, expectedValue.toBigDecimal(), precision)
}
