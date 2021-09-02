package io.kotest.engine.test.interceptors

import io.kotest.assertions.all
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainInOrder

private object Boom {
   override fun toString() = "BOOM"
}

private class CannotLogException(override val message: String) : Exception()

@Isolate
@OptIn(ExperimentalKotest::class)
class CoroutineLoggingTestExecutionInterceptorSpec : FunSpec({
   concurrency = 1

   val console = object : LogExtension {
      val stored = mutableListOf<String>()

      override suspend fun handleLogs(testCase: TestCase, logs: List<Any>) {
         stored.addAll(logs.map { it.toString() })
      }
   }

   val database = object : LogExtension {
      val stored = mutableListOf<String>()

      override suspend fun handleLogs(testCase: TestCase, logs: List<Any>) {
         stored.addAll(logs.map { when (it) {
            is Boom -> throw CannotLogException("danger zone")
            else -> it.toString()
         }})
      }
   }

   val listeners = listOf(console, database)
   val logLevel = configuration.logLevel

   beforeSpec {
      configuration.registerExtensions(listeners)
   }

   afterSpec {
      configuration.deregisterExtensions(listeners)
      configuration.logLevel = logLevel
   }

   fun reset(level: LogLevel) {
      configuration.logLevel = level

      console.stored.clear()
      database.stored.clear()

      console.stored.shouldBeEmpty()
      database.stored.shouldBeEmpty()
   }

   context("suppresses exceptions thrown by consume functions") {
      reset(LogLevel.Error)

      test("execute logs") {
         shouldThrow<CannotLogException> { error { Boom } }
         error { "this is fine" }
      }

      database.stored.shouldContainExactly("this is fine")
   }

   context("ignores all logs when logging is OFF by config") {
      reset(LogLevel.Off)

      test("execute logs") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }

      all {
         console.stored.shouldBeEmpty()
         database.stored.shouldBeEmpty()
      }
   }

   context("ignores logs lower priority than ERROR by config") {
      reset(LogLevel.Error)

      test("execute logs") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }

      val expected = setOf("error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   context("ignores logs lower priority than WARN by config") {
      reset(LogLevel.Warn)

      test("execute logs") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }

      val expected = setOf("warn", "error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   context("ignores logs lower priority than INFO by config") {
      reset(LogLevel.Debug)

      test("execute logs") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }

      val expected = setOf("info", "warn", "error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   context("accepts all logs when DEBUG by config") {
      reset(LogLevel.Debug)

      test("execute logs") {
         info { "info" }
         warn { "warn" }
         error { "error" }
         debug { "debug" }
      }

      val expected = setOf("info", "warn", "error", "debug")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   context("accepts all logs when TRACE by config using TestLogger") {
      reset(LogLevel.Trace)

      test("execute logs") {
         logger.info { "info" }
         logger.warn { "warn" }
         logger.error { "error" }
         logger.debug { "debug" }
         logger.trace { "trace" }
      }

      val expected = setOf("info", "warn", "error", "debug", "trace")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }
})
