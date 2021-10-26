package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.config.configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.logging.LogEntry
import io.kotest.engine.test.logging.LogExtension
import io.kotest.engine.test.logging.debug
import io.kotest.engine.test.logging.error
import io.kotest.engine.test.logging.info
import io.kotest.engine.test.logging.warn
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

class DatabaseLogExtension : LogExtension {
   val database = mutableListOf<String>()
   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
      database.addAll(logs.map { it.message.toString() })
   }
}

@ExperimentalKotest
object FailingLogExtension : LogExtension {
   var invoked = false
   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
      invoked = true
      throw CannotLogException("danger zone")
   }
}

@ExperimentalKotest
class TestWithFailingLog : FunSpec() {
   init {
      register(FailingLogExtension)
      beforeTest {
         FailingLogExtension.invoked shouldBe false
         coroutineContext.configuration.logLevel = LogLevel.Debug
      }
      test("should suppress exceptions thrown by log extensions") {
         debug { "wobble" }
      }
      afterTest { FailingLogExtension.invoked shouldBe true }
   }
}

@ExperimentalKotest
class TestIgnoreWhenLoggingOff : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Off
      }
      test("ignores all logs when logging is OFF by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }
      afterTest {
         database.database.shouldBeEmpty()
      }
   }
}

class CoroutineLoggingInterceptorTest : FunSpec({
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
})

private class CannotLogException(override val message: String) : Exception()
