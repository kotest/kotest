package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel

typealias LogFn = () -> Any

data class LogEntry(val level: LogLevel, val message: Any)

@ExperimentalKotest
class TestLogger(private val logLevel: LogLevel) {
   internal val logs = mutableListOf<LogEntry>()
   internal fun maybeLog(message: LogFn, level: LogLevel) {
      if (level >= logLevel) {
         logs.add(LogEntry(level, message()))
      }
   }
}
