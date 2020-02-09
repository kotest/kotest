package io.kotest.property.arbitrary

import kotlin.js.Date

fun Arb.Companion.date(minYear: Int = 1970, maxYear: Int = 2030) = arb {

   val randomMonth = it.random.nextInt(1, 12)
   val randomDay = when (randomMonth) {
      2 -> it.random.nextInt(1, 29)
      4, 6, 9, 11 -> it.random.nextInt(1, 31)
      else -> it.random.nextInt(1, 32)
   }
   val randomYear = it.random.nextInt(minYear, maxYear + 1)
   Date(randomYear, randomMonth, randomDay)

}

fun Arb.Companion.datetime(minYear: Int = 1970, maxYear: Int = 2030) = arb {

   val randomMonth = it.random.nextInt(1, 12)
   val randomDay = when (randomMonth) {
      2 -> it.random.nextInt(1, 29)
      4, 6, 9, 11 -> it.random.nextInt(1, 31)
      else -> it.random.nextInt(1, 32)
   }
   val randomYear = it.random.nextInt(minYear, maxYear + 1)

   val randomHour = it.random.nextInt(0, 24)
   val randomMinute = it.random.nextInt(0, 60)
   val randomSecond = it.random.nextInt(0, 60)

   Date(randomYear, randomMonth, randomDay, randomHour, randomMinute, randomSecond)
}
