package io.kotest.property.arbitrary.duration

import io.kotest.property.Classifier
import kotlin.time.Duration
import kotlin.time.DurationUnit

class DurationClassifier(
   private val range: ClosedRange<Duration>
) : Classifier<Pair<Duration, DurationUnit>> {
   override fun classify(value: Pair<Duration, DurationUnit>): String? =
      when (value.first) {
          range.start -> "MIN"
          range.endInclusive -> "MAX"
          Duration.ZERO -> "ZERO"
          Duration.INFINITE -> "INFINITY"
          -Duration.INFINITE -> "NEGATIVE INFINITY"
          else -> null
      }
}
