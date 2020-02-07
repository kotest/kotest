package io.kotest.property.arbitrary

actual fun targetDefaultForClassName(className: String): Arb<*>? {
   return when (className) {
      "java.time.LocalDate" -> Arb.localDate()
      "java.time.LocalDateTime" -> Arb.localDateTime()
      "java.time.LocalTime" -> Arb.localTime()
      "java.time.Period" -> Arb.period()
      else -> null
   }
}
