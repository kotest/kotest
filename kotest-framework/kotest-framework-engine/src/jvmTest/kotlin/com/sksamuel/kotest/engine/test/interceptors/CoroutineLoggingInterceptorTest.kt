//package com.sksamuel.kotest.engine.test.interceptors
//
//import io.kotest.assertions.all
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.config.LogLevel
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.TestCase
//import io.kotest.engine.test.logging.LogEntry
//import io.kotest.engine.test.logging.LogExtension
//import io.kotest.engine.test.logging.TestLogger
//import io.kotest.engine.test.logging.debug
//import io.kotest.engine.test.logging.error
//import io.kotest.engine.test.logging.info
//import io.kotest.engine.test.logging.logger
//import io.kotest.engine.test.logging.trace
//import io.kotest.engine.test.logging.warn
//import io.kotest.matchers.collections.shouldBeEmpty
//import io.kotest.matchers.collections.shouldContainInOrder
//
//private val console = object : LogExtension {
//   fun reset() = stored.clear()
//   val logs get() = stored.toList()
//   private val stored = mutableListOf<String>()
//
//   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
//      stored.addAll(logs.map { it.message.toString() })
//      return
//   }
//}
//
//private val database = object : LogExtension {
//   fun reset() = stored.clear()
//   val logs get() = stored.toList()
//   private val stored = mutableListOf<String>()
//
//   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
//      stored.addAll(logs.map { it.message }.map { when (it) {
//         is Boom -> throw CannotLogException("danger zone")
//         else -> it.toString()
//      }})
//      return
//   }
//}
//
//class CoroutineLoggingInterceptorTest : FunSpec({
//
//   val logLevel = configuration.logLevel
//   val extensions = listOf(console, database)
//
//   beforeSpec {
//      configuration.registerExtensions(extensions)
//      reset(logLevel)
//   }
//
//   afterSpec {
//      configuration.deregisterExtensions(extensions)
//      reset(logLevel)
//   }
//
//   context("suppresses exceptions thrown by consume functions") {
//      reset(LogLevel.Error)
//
//      test("execute") {
//         shouldThrow<CannotLogException> {
//            database.handleLogs(testCase, listOf(LogEntry(LogLevel.Error, Boom)))
//         }
//         error { Boom }
//      }
//
//      database.logs.shouldBeEmpty()
//   }
//
//   context("ignores all logs when logging is OFF by config") {
//      reset(LogLevel.Off)
//
//      test("execute") {
//         info { "info" }
//         warn { "warn" }
//         error { "error" }
//         debug { "debug" }
//      }
//
//      all {
//         console.logs.shouldBeEmpty()
//         database.logs.shouldBeEmpty()
//      }
//   }
//
//   context("ignores logs lower priority than ERROR by config") {
//      reset(LogLevel.Error)
//
//      test("execute") {
//         info { "info" }
//         warn { "warn" }
//         error { "error" }
//         debug { "debug" }
//      }
//
//      all {
//         console.logs.shouldContainInOrder("error")
//         database.logs.shouldContainInOrder("error")
//      }
//   }
//
//   context("ignores logs lower priority than WARN by config") {
//      reset(LogLevel.Warn)
//
//      test("execute") {
//         info { "info" }
//         warn { "warn" }
//         error { "error" }
//         debug { "debug" }
//      }
//
//      val expected = arrayOf("warn", "error")
//
//      all {
//         console.logs.shouldContainInOrder(*expected)
//         database.logs.shouldContainInOrder(*expected)
//      }
//   }
//
//   context("ignores logs lower priority than INFO by config") {
//      reset(LogLevel.Debug)
//
//      test("execute") {
//         info { "info" }
//         warn { "warn" }
//         error { "error" }
//         debug { "debug" }
//      }
//
//      val expected = arrayOf("info", "warn", "error")
//
//      all {
//         console.logs.shouldContainInOrder(*expected)
//         database.logs.shouldContainInOrder(*expected)
//      }
//   }
//
//   context("accepts all logs when DEBUG by config") {
//      reset(LogLevel.Debug)
//
//      test("execute") {
//         info { "info" }
//         warn { "warn" }
//         error { "error" }
//         debug { "debug" }
//      }
//
//      val expected = arrayOf("info", "warn", "error", "debug")
//
//      all {
//         console.logs.shouldContainInOrder(*expected)
//         database.logs.shouldContainInOrder(*expected)
//      }
//   }
//
//   context("accepts all logs when TRACE by config using TestLogger") {
//      reset(LogLevel.Trace)
//
//      test("execute logs") {
//         logAll(logger!!)
//      }
//
//      val expected = arrayOf("info", "warn", "error", "debug", "trace")
//
//      all {
//         console.logs.shouldContainInOrder(*expected)
//         database.logs.shouldContainInOrder(*expected)
//      }
//   }
//})
//
//@OptIn(ExperimentalKotest::class)
//private fun logAll(logger: TestLogger) {
//   logger.info { "info" }
//   logger.warn { "warn" }
//   logger.error { "error" }
//   logger.debug { "debug" }
//   logger.trace { "trace" }
//}
//
//private object Boom {
//   override fun toString() = "BOOM"
//}
//
//private class CannotLogException(override val message: String) : Exception()
//
//private fun reset(level: LogLevel) {
//   configuration.logLevel = level
//
//   console.reset()
//   database.reset()
//
//   console.logs.shouldBeEmpty()
//   database.logs.shouldBeEmpty()
//}
//
