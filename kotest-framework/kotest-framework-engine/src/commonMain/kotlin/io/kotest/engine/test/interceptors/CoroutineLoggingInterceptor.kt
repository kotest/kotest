package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.LogLevel
import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * An extension that is invoked when a test completes, logging any values that were logged using
 * by way of [trace], [debug], [info], [warn], and [error] during that test.
 *
 * Users can use testId on [TestCase.description] to cross-reference the [TestResult] with the provided logs.
 */
@ExperimentalKotest
interface LogExtension : Extension {
   suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>)
}

/**
 * [SerialLogExtension] wraps a [LogExtension] with a mutex, so we can guarantee
 * that calls to [LogExtension.handleLogs] are invoked sequentially.
 */
@ExperimentalKotest
internal class SerialLogExtension constructor(private val logExtension: LogExtension) {
   private val mutex = Mutex()

   suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) = mutex.withLock {
      runCatching {
         logExtension.handleLogs(testCase, logs)
      }
   }
}

/**
 * A [LogExtension] that writes to std out using [println].
 */
@ExperimentalKotest
object ConsoleLogExtension : LogExtension {
   override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
      println(" - ${testCase.description}")
      logs.forEach { println(it.level.name + ": " + it.message()) }
   }
}

@ExperimentalKotest
private class TestContextLoggingCoroutineContextElement(val logger: TestLogger) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestContextLoggingCoroutineContextElement>
}

@OptIn(ExperimentalKotest::class)
internal class CoroutineLoggingInterceptor(private val extensions: List<SerialLogExtension>) : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      when {
         configuration.logLevel.isDisabled() || extensions.isEmpty() -> test(testCase, context)
         else -> {
            val logger = DefaultTestLogger()
            val contextWithLogging = context.withCoroutineContext(TestContextLoggingCoroutineContextElement(logger))
            try {
               test(testCase, contextWithLogging)
            } catch (ex: Exception) {
               throw ex
            } finally {
               val logs = logger.logs.filter { it.level >= configuration.logLevel }
               extensions.forEach { extension -> extension.handleLogs(testCase, logs) }
            }
         }
      }
   }
}

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Trace].
 */
@ExperimentalKotest
suspend fun TestContext.trace(message:  LogFn) = logger.trace(message)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Debug].
 */
@ExperimentalKotest
suspend fun TestContext.debug(message:  LogFn) = logger.debug(message)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Info] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.info(message:  LogFn) = logger.info(message)

/**
 * Appends to the [TestContext] log, when the log level is [io.kotest.core.config.LogLevel.Warn] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.warn(message:  LogFn) = logger.warn(message)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Error] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.error(message:  LogFn) = logger.error(message)

typealias LogFn = suspend () -> Any?

@ExperimentalKotest
interface TestLogger {
   suspend fun trace(message: LogFn)
   suspend fun debug(message: LogFn)
   suspend fun info(message: LogFn)
   suspend fun warn(message: LogFn)
   suspend fun error(message: LogFn)
}

@ExperimentalKotest
class DefaultTestLogger : TestLogger {
   val logs = mutableListOf<LogEntry>()

   override suspend fun trace(message: LogFn) {
      logs.add(LogEntry(LogLevel.Trace, message))
   }

   override suspend fun debug(message: LogFn) {
      logs.add(LogEntry(LogLevel.Debug, message))
   }

   override suspend fun info(message: LogFn) {
      logs.add(LogEntry(LogLevel.Info, message))
   }

   override suspend fun warn(message: LogFn) {
      logs.add(LogEntry(LogLevel.Warn, message))
   }

   override suspend fun error(message: LogFn) {
      logs.add(LogEntry(LogLevel.Error, message))
   }
}

data class LogEntry(val level: LogLevel, val message: LogFn)

/**
 * Returns the [TestLogger] that is embedded with this [TestContext].
 */
@ExperimentalKotest
val TestContext.logger: TestLogger
   get() = coroutineContext[TestContextLoggingCoroutineContextElement]?.logger ?: error("No test logger in context")
