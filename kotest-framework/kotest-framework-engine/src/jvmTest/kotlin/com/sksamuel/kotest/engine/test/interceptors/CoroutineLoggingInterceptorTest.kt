package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.engine.config.configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.logging.LogEntry
import io.kotest.engine.test.logging.LogExtension
import io.kotest.engine.test.logging.debug
import io.kotest.engine.test.logging.error
import io.kotest.engine.test.logging.info
import io.kotest.engine.test.logging.trace
import io.kotest.engine.test.logging.warn
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

@ExperimentalKotest
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
class ShouldLogWithTestAndProjectLevelExtensions : FunSpec() {
   init {
      val db1 = DatabaseLogExtension()
      val db2 = DatabaseLogExtension()
      register(db1)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Debug
         coroutineContext.configuration.registry.add(db2)
      }
      test("should log to project and test extensions") {
         debug { "wobble" }
      }
      afterTest {
         db1.database.shouldContain("wobble")
         db2.database.shouldContain("wobble")
      }
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

@ExperimentalKotest
class TestErrorLogLevel : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Error
      }
      test("ignores logs lower priority than ERROR by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
         trace { "trace" }
      }
      afterTest {
         database.database.shouldBe(listOf("error"))
      }
   }
}

@ExperimentalKotest
class TestDebugLogLevel : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Debug
      }
      test("ignores logs lower priority than DEBUG by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
         trace { "trace" }
      }
      afterTest {
         database.database.shouldBe(listOf("info", "warn", "error", "debug"))
      }
   }
}

@ExperimentalKotest
class TestTraceLogLevel : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Trace
      }
      test("ignores logs lower priority than TRACE by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
         trace { "trace" }
      }
      afterTest {
         database.database.shouldBe(listOf("info", "warn", "error", "debug", "trace"))
      }
   }
}

@ExperimentalKotest
class TestInfoLogLevel : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Info
      }
      test("ignores logs lower priority than INFO by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
         trace { "trace" }
      }
      afterTest {
         database.database.shouldBe(listOf("info", "warn", "error"))
      }
   }
}

@ExperimentalKotest
class TestWarnLogLevel : FunSpec() {
   init {
      val database = DatabaseLogExtension()
      register(database)
      beforeTest {
         coroutineContext.configuration.logLevel = LogLevel.Warn
      }
      test("ignores logs lower priority than INFO by config") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
         trace { "trace" }
      }
      afterTest {
         database.database.shouldBe(listOf("warn", "error"))
      }
   }
}

private class CannotLogException(override val message: String) : Exception()
