package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.arbitrary.localDateTimeChanges
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.LocalDateTimeChange
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeChangesTest: StringSpec() {
   init {
       "works for Central time" {
          ZoneId.of("America/Chicago").localDateTimeChanges(
             LocalDateTime.of(2025, 9, 12, 1, 2,3)
          ).take(2).toList() shouldBe listOf(
             LocalDateTimeChange(
                dateTimeBefore = LocalDateTime.of(2025, 11, 2, 2, 0,0),
                dateTimeAfter = LocalDateTime.of(2025, 11, 2, 1, 0,0),
                type = LocalDateTimeChange.LocalDateTimeChangeType.OVERLAP,
             ),
             LocalDateTimeChange(
                dateTimeBefore = LocalDateTime.of(2026, 3, 8, 2, 0,0),
                dateTimeAfter = LocalDateTime.of(2026, 3, 8, 3, 0,0),
                type = LocalDateTimeChange.LocalDateTimeChangeType.GAP,
             ),
          )
       }
      "works for UTC - no changes" {
         ZoneId.of("UTC").localDateTimeChanges(
            LocalDateTime.of(2025, 9, 12, 1, 2,3)
         ).toList() shouldBe listOf()
      }
   }
}
