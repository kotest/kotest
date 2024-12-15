package io.kotest.core.config

sealed class LogLevel(val level: Int, val name: String) {
   object Off : LogLevel(Int.MAX_VALUE, "off")
   object Error : LogLevel(4, "error")
   object Warn : LogLevel(3, "warn")
   object Info : LogLevel(2, "info")
   object Debug : LogLevel(1, "debug")
   object Trace : LogLevel(0, "trace")

   fun isDisabled() = this is Off

   operator fun compareTo(other: LogLevel): Int = when {
      level < other.level -> -1
      level > other.level -> 1
      else -> 0
   }

   companion object {
      fun from(level: String?): LogLevel = when (level) {
         "trace" -> Trace
         "debug" -> Debug
         "warn" -> Warn
         "info" -> Info
         "error" -> Error
         else -> Off
      }
   }
}
