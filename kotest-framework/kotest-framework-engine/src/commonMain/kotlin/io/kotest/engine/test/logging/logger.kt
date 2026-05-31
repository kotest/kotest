@file:Suppress("DEPRECATION")

package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel

internal const val LOGGING_DEPRECATION_MESSAGE =
   "The Kotest test logging feature (LogExtension and the trace/debug/info/warn/error log functions) " +
      "is deprecated and will be removed in a future release."

@Deprecated(LOGGING_DEPRECATION_MESSAGE)
typealias LogFn = () -> Any

@Deprecated(LOGGING_DEPRECATION_MESSAGE)
data class LogEntry(val level: LogLevel, val message: Any)

@Deprecated(LOGGING_DEPRECATION_MESSAGE)
@ExperimentalKotest
class TestLogger(private val logLevel: LogLevel) {
   internal val logs = mutableListOf<LogEntry>()
   internal fun maybeLog(message: LogFn, level: LogLevel) {
      if (level >= logLevel) {
         logs.add(LogEntry(level, message()))
      }
   }
}
