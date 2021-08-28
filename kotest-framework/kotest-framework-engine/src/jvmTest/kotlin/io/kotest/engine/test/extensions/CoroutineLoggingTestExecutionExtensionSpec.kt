package io.kotest.engine.test.extensions

import io.kotest.assertions.all
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.interceptors.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainInOrder

private object Boom {
   override fun toString() = "BOOM"
}

private class CannotLogException(override val message: String) : Exception()

@Isolate
@OptIn(ExperimentalKotest::class)
class CoroutineLoggingTestExecutionExtensionSpec : FunSpec({
   val console = object : LogListener {
      override val name = "TestConsoleLogListener"
      val stored = mutableListOf<String>()

      override suspend fun consume(testCase: TestCase, logs: List<Any>) {
         stored.addAll(logs.map { it.toString() })
      }
   }

   val database = object : LogListener {
      override val name: String = "TestDatabaseLogListener"
      val stored = mutableListOf<String>()

      override suspend fun consume(testCase: TestCase, logs: List<Any>) {
         stored.addAll(logs.map { when (it) {
            is Boom -> throw CannotLogException("danger zone")
            else -> it.toString()
         }})
      }
   }

   val listeners = listOf(console, database)
   val logLevel = configuration.logLevel

   beforeSpec {
      configuration.registerListeners(listeners)
   }

   afterSpec {
      configuration.deregisterListeners(listeners)
      configuration.logLevel = logLevel
   }

   beforeTest {
      console.stored.clear()
      database.stored.clear()

      console.stored.shouldBeEmpty()
      database.stored.shouldBeEmpty()
   }

   test("suppresses exceptions thrown by consume functions") {
      configuration.logLevel = LogLevel.ERROR

      database.stored.shouldBeEmpty()
      shouldThrow<CannotLogException> { error { Boom } }
      database.stored.shouldBeEmpty()

      error { "this is fine" }
      database.stored.shouldContainExactly("this is fine")
   }

   test("ignores all logs when logging is OFF by config") {
      configuration.logLevel = LogLevel.OFF

      info { "info" }
      warn { "warn" }
      error { "error" }
      debug { "debug" }

      val expected = setOf<String>()

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   test("ignores logs lower priority than ERROR by config") {
      configuration.logLevel = LogLevel.ERROR

      info { "info" }
      warn { "warn" }
      error { "error" }
      debug { "debug" }

      val expected = setOf("error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   test("ignores logs lower priority than WARN by config") {
      configuration.logLevel = LogLevel.WARN

      info { "info" }
      warn { "warn" }
      error { "error" }
      debug { "debug" }

      val expected = setOf("warn", "error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   test("ignores logs lower priority than INFO by config") {
      configuration.logLevel = LogLevel.DEBUG

      info { "info" }
      warn { "warn" }
      error { "error" }
      debug { "debug" }

      val expected = setOf("info", "warn", "error")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }
   }

   test("accepts all logs when DEBUG by config") {
      configuration.logLevel = LogLevel.DEBUG

      info { "info" }
      warn { "warn" }
      error { "error" }
      debug { "debug" }

      val expected = setOf("info", "warn", "error", "debug")

      all {
         console.stored.shouldContainInOrder(expected)
         database.stored.shouldContainInOrder(expected)
      }

   }
})
