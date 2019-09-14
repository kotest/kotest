import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.sqrt

fun <T : Number> Collection<T>.shouldHaveMean(value: BigDecimal, precision: Int = defaultPrecision) = this should haveMean<T>(value, precision)
fun <T : Number> Collection<T>.shouldHaveMean(value: Double, precision: Int = defaultPrecision) = this should haveMean<T>(value, precision)

fun <T : Number> Collection<T>.shouldNotHaveMean(value: BigDecimal, precision: Int = defaultPrecision) = this shouldNot haveMean<T>(value, precision)
fun <T : Number> Collection<T>.shouldNotHaveMean(value: Double, precision: Int = defaultPrecision) = this shouldNot haveMean<T>(value, precision)

fun <T : Number> Collection<T>.shouldHaveVariance(value: BigDecimal, precision: Int = defaultPrecision) = this should haveVariance<T>(value, precision)
fun <T : Number> Collection<T>.shouldHaveVariance(value: Double, precision: Int = defaultPrecision) = this should haveVariance<T>(value, precision)

fun <T : Number> Collection<T>.shouldNotHaveVariance(value: BigDecimal, precision: Int = defaultPrecision) = this shouldNot haveVariance<T>(value, precision)
fun <T : Number> Collection<T>.shouldNotHaveVariance(value: Double, precision: Int = defaultPrecision) = this shouldNot haveVariance<T>(value, precision)

fun <T : Number> Collection<T>.shouldHaveStandardDeviation(value: BigDecimal, precision: Int = defaultPrecision) = this should haveStandardDeviation<T>(value, precision)
fun <T : Number> Collection<T>.shouldHaveStandardDeviation(value: Double, precision: Int = defaultPrecision) = this should haveStandardDeviation<T>(value, precision)

fun <T : Number> Collection<T>.shouldNotHaveStandardDeviation(value: BigDecimal, precision: Int = defaultPrecision) = this shouldNot haveStandardDeviation<T>(value, precision)
fun <T : Number> Collection<T>.shouldNotHaveStandardDeviation(value: Double, precision: Int = defaultPrecision) = this shouldNot haveStandardDeviation<T>(value, precision)

private val defaultMathContext = MathContext(64, RoundingMode.HALF_UP)
private const val defaultPrecision = 4
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
      "Collection should have mean $expected but was $actual",
      "Collection should not have mean $expected but was $actual"
   )
}

private fun <T : Number> testVariance(collection: Collection<T>, expectedValue: BigDecimal, precision: Int): MatcherResult {
   val expected = expectedValue.stripTrailingZeros()
   val actual = if (collection.isEmpty()) BigDecimal.ZERO else calculateVariance(collection).round(precision)
   return MatcherResult(
      expected.compareTo(actual) == 0,
      "Collection should have variance $expected but was $actual",
      "Collection should not have variance $expected but was $actual"
   )
}

private fun <T : Number> testStandardDeviation(collection: Collection<T>, expectedValue: BigDecimal, precision: Int): MatcherResult {
   val expected = expectedValue.stripTrailingZeros()
   val actual = if (collection.isEmpty()) BigDecimal.ZERO else calculateStandardDeviation(collection).round(precision)
   return MatcherResult(
      expected.compareTo(actual) == 0,
      "Collection should have standard deviation $expected but was $actual",
      "Collection should not have standard deviation $expected but was $actual"
   )
}

fun <T : Number> haveMean(expectedValue: BigDecimal, precision: Int = defaultPrecision) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = testMean(value, expectedValue, precision)
}

fun <T : Number> haveMean(expectedValue: Double, precision: Int = defaultPrecision) = object : Matcher<Collection<T>> {
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




