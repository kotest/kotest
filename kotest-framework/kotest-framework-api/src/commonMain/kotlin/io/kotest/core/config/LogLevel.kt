package io.kotest.core.config

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.env

enum class LogLevel(val level: Int) {
   OFF(0), ERROR(1), WARN(2), INFO(3), DEBUG(4);

   fun isDebugEnabled() = level >= DEBUG.level
   fun isInfoEnabled() = level >= INFO.level
   fun isWarnEnabled() = level >= WARN.level
   fun isErrorEnabled() = level >= ERROR.level
   fun isDisabled() = level < 1 || level > 4

   companion object {
      fun from(prop: String?): LogLevel = when (prop ?: env(KotestEngineProperties.logLevel) ?: "off") {
         "debug" -> LogLevel.DEBUG
         "info" -> LogLevel.INFO
         "warn" -> LogLevel.WARN
         "error" -> LogLevel.ERROR
         "off" -> LogLevel.OFF
         else -> LogLevel.OFF
      }
   }
}
