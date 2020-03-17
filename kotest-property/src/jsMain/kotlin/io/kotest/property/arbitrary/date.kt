package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.js.Date
import kotlin.random.nextInt

fun Arb.Companion.date(minYear: Int = 1970, maxYear: Int = 2030) = date(minYear..maxYear)
fun Arb.Companion.date(yearRange: IntRange) = Arb.create {
   val randomMonth = it.random.nextInt(1, 12)
   val randomDay = when (randomMonth) {
      2 -> it.random.nextInt(1, 29)
      4, 6, 9, 11 -> it.random.nextInt(1, 31)
      else -> it.random.nextInt(1, 32)
   }
   val randomYear = it.random.nextInt(yearRange)
   Date(randomYear, randomMonth, randomDay)
}

fun Arb.Companion.datetime(minYear: Int = 1970, maxYear: Int = 2030) = datetime(minYear..maxYear)
fun Arb.Companion.datetime(yearRange: IntRange) = Arb.create {
   val randomMonth = it.random.nextInt(1, 12)
   val randomDay = when (randomMonth) {
      2 -> it.random.nextInt(1, 29)
      4, 6, 9, 11 -> it.random.nextInt(1, 31)
      else -> it.random.nextInt(1, 32)
   }
   val randomYear = it.random.nextInt(yearRange)

   val randomHour = it.random.nextInt(0, 24)
   val randomMinute = it.random.nextInt(0, 60)
   val randomSecond = it.random.nextInt(0, 60)

   Date(randomYear, randomMonth, randomDay, randomHour, randomMinute, randomSecond)
}
