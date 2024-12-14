package io.kotest.matchers.equality

import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import java.lang.AssertionError
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.time.Duration

sealed interface CustomComparisonResult {
   val comparable: Boolean
   data object NotComparable: CustomComparisonResult {
      override val comparable = false
   }
   data object Equal: CustomComparisonResult {
      override val comparable = true
   }
   data class Different(val assertionError: AssertionError): CustomComparisonResult {
      override val comparable = true
   }
}

fun interface Assertable {
   fun assert(expected: Any?, actual: Any?): CustomComparisonResult
}

inline fun<reified T: Any> customComparison(
   expected: Any?,
   actual: Any?,
   assertion: (expected: T, actual: T) -> Unit
): CustomComparisonResult = when {
   expected == null -> CustomComparisonResult.NotComparable
   actual == null -> CustomComparisonResult.NotComparable
   expected is T && actual is T -> {
      try {
         assertion(expected, actual)
         CustomComparisonResult.Equal
      } catch (e: AssertionError) {
         CustomComparisonResult.Different(e)
      }
   }
   else -> CustomComparisonResult.NotComparable
}

fun matchDoublesWithTolerance(tolerance: Double) = Assertable { expected: Any?, actual: Any? ->
   customComparison<Double>(expected, actual) { expected: Double, actual: Double ->
      actual shouldBe (expected plusOrMinus tolerance)
   }
}

fun matchBigDecimalsIgnoringScale() = Assertable { expected: Any?, actual: Any? ->
   customComparison<BigDecimal>(expected, actual) { expected: BigDecimal, actual: BigDecimal ->
      actual shouldBeEqualIgnoringScale expected
   }
}

fun matchLocalDateTimesWithTolerance(tolerance: Duration) = Assertable { expected: Any?, actual: Any? ->
   customComparison<LocalDateTime>(expected, actual) { expected: LocalDateTime, actual: LocalDateTime ->
      actual shouldBe (expected plusOrMinus tolerance)
   }
}

fun matchZonedDateTimesWithTolerance(tolerance: Duration) = Assertable { expected: Any?, actual: Any? ->
   customComparison<ZonedDateTime>(expected, actual) { expected: ZonedDateTime, actual: ZonedDateTime ->
      actual shouldBe (expected plusOrMinus tolerance)
   }
}

fun matchOffsetDateTimesWithTolerance(tolerance: Duration) = Assertable { expected: Any?, actual: Any? ->
   customComparison<OffsetDateTime>(expected, actual) { expected: OffsetDateTime, actual: OffsetDateTime ->
      actual shouldBe (expected plusOrMinus tolerance)
   }
}

fun matchInstantsWithTolerance(tolerance: Duration) = Assertable { expected: Any?, actual: Any? ->
   customComparison<Instant>(expected, actual) { expected: Instant, actual: Instant ->
      actual shouldBe (expected plusOrMinus tolerance)
   }
}

val matchStringsIgnoringCase = Assertable { expected: Any?, actual: Any? ->
   customComparison<String>(expected, actual) { expected: String, actual: String ->
      actual shouldBeEqualIgnoringCase expected
   }
}

fun<T> matchListsIgnoringOrder() = Assertable { expected: Any?, actual: Any? ->
   customComparison<List<T>>(expected, actual) { expected: List<T>, actual: List<T> ->
      actual shouldContainExactlyInAnyOrder expected
   }
}
