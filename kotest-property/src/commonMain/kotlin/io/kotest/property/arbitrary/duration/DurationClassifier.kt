package io.kotest.property.arbitrary.duration

import io.kotest.property.Classifier
import kotlin.time.Duration

class DurationClassifier(
   private val range: ClosedRange<Duration>
) : Classifier<Duration> {
   override fun classify(value: Duration): String? =
      when (value) {
          range.start -> "MIN"
          range.endInclusive -> "MAX"
          Duration.ZERO -> "ZERO"
          Duration.INFINITE -> "INFINITY"
          -Duration.INFINITE -> "NEGATIVE INFINITY"
          else -> null
      }
}
