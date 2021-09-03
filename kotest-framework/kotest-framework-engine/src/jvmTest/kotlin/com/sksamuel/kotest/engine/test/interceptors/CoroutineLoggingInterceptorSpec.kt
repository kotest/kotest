package io.kotest.engine.test.interceptors

import io.kotest.assertions.all
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.config.LogLevel
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestId
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldNotBeEmpty

object IReallyNeedToRememberToUseTagsWhenTestingTestsThatTestUsingEngine : Tag()

private object Boom {
   override fun toString() = "BOOM"
}

private class CannotLogException(override val message: String) : Exception()

@Isolate
@OptIn(ExperimentalKotest::class)
class CoroutineLoggingInterceptorSpec : FunSpec({
   tags(IReallyNeedToRememberToUseTagsWhenTestingTestsThatTestUsingEngine)

   val logLevel = configuration.logLevel

   beforeSpec {
      isPrivateCoroutineLoggingInterceptorSpecEnabled = true
      configuration.registerExtensions(extensions)
      reset(logLevel)
   }

   afterSpec {
      isPrivateCoroutineLoggingInterceptorSpecEnabled = false
      configuration.deregisterExtensions(extensions)
      reset(logLevel)
   }

   test("All tests within PrivateCoroutineLoggingInterceptorSpec succeed") {
      val started = mutableListOf<TestId>()
      val passed = mutableListOf<TestId>()

      val listener = object : TestEngineListener {
         override suspend fun testStarted(testCase: TestCase) {
            started.add(testCase.description.testId)
         }

         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            if (result.status == TestStatus.Success) {
               passed.add(testCase.description.testId)
            } else {
               fail(result.toString())
            }
         }
      }

      val result = KotestEngineLauncher()
         .withListener(listener)
         .withSpec(PrivateCoroutineLoggingInterceptorSpec::class)
         .launch()

      result.errors.shouldBeEmpty()
      started shouldContainExactlyInAnyOrder passed
      passed.shouldNotBeEmpty()
   }
})

private val console = object : LogExtension {
   val stored = mutableListOf<String>()

   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
   }
}

private val database = object : LogExtension {
   val stored = mutableListOf<String>()
   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
   }

//   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
//      stored.addAll(logs.map { it.message() }.map { when (it) {
//         is Boom -> throw CannotLogException("danger zone")
//         else -> it.toString()
//      }})
//   }
}

private fun reset(level: LogLevel) {
   configuration.logLevel = level

   console.stored.clear()
   database.stored.clear()

   console.stored.shouldBeEmpty()
   database.stored.shouldBeEmpty()
}

private val extensions = listOf(console, database)

private var isPrivateCoroutineLoggingInterceptorSpecEnabled = false

//TODO: refactor this class away, we needed it when we checked the configuration for listeners once
@OptIn(ExperimentalKotest::class)
private class PrivateCoroutineLoggingInterceptorSpec : FunSpec({
   concurrency = 1

   defaultTestConfig = TestCaseConfig(enabled = isPrivateCoroutineLoggingInterceptorSpecEnabled)

   tags(IReallyNeedToRememberToUseTagsWhenTestingTestsThatTestUsingEngine)

   context("suppresses exceptions thrown by consume functions") {
      reset(LogLevel.Error)

      test("execute logs") {
         shouldThrow<CannotLogException> { error { Boom } }
         error {
            "this is fine"
         }
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
