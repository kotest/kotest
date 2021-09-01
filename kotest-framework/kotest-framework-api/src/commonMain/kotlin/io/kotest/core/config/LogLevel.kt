package io.kotest.core.config

enum class LogLevel(val level: Int) {
   OFF(0), ERROR(1), WARN(2), INFO(3), DEBUG(4);

   fun isDebugEnabled() = level >= DEBUG.level
   fun isInfoEnabled() = level >= INFO.level
   fun isWarnEnabled() = level >= WARN.level
   fun isErrorEnabled() = level >= ERROR.level
   fun isDisabled() = level < 1 || level > 4

   companion object {
      fun from(level: String?): LogLevel = when (level) {
         "debug" -> DEBUG
         "info" -> INFO
         "warn" -> WARN
         "error" -> ERROR
         "off" -> OFF
         else -> OFF
      }
   }
}
