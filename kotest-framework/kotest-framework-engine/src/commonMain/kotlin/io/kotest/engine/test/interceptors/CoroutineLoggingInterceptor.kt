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

data class LogEntry(val level: LogLevel, val message: Any)

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
      logs.forEach { println(it.level.name + ": " + it.message) }
   }
}

/**
 * Returns the [TestLogger] that is embedded with this [TestContext].
 * Does not error when there isn't a logger, there isn't one when LogLevel is set to Off.
 */
@ExperimentalKotest
internal val TestContext.logs: MutableList<LogEntry>?
   get() = coroutineContext[TestContextLoggingCoroutineContextElement]?.logs

@ExperimentalKotest
private class TestContextLoggingCoroutineContextElement(val logs: MutableList<LogEntry>) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestContextLoggingCoroutineContextElement>
}

@OptIn(ExperimentalKotest::class)
internal object CoroutineLoggingInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      val extensions = configuration.extensions().filterIsInstance<LogExtension>().map { SerialLogExtension(it) }

      when {
         configuration.logLevel.isDisabled() || extensions.isEmpty() -> test(testCase, context)
         else -> {
            val logs = mutableListOf<LogEntry>()
            val contextWithLogging = context.withCoroutineContext(TestContextLoggingCoroutineContextElement(logs))
            try {
               test(testCase, contextWithLogging)
            } catch (ex: Exception) {
               throw ex
            } finally {
               val defensiveCopy = logs.toList()
               extensions.forEach { extension ->
                  runCatching {
                     extension.handleLogs(testCase, defensiveCopy)
                  }
               }
            }
         }
      }
   }
}


typealias LogFn = suspend () -> Any

@ExperimentalKotest
internal val TestContext.logger: TestLogger
   get() = TestLogger(logs = logs ?: mutableListOf())

@ExperimentalKotest
class TestLogger(internal val logs: MutableList<LogEntry>) {
   internal suspend fun maybeLog(message: LogFn, level: LogLevel) {
      if (level >= configuration.logLevel) {
         logs.apply {
            add(LogEntry(level, message.invoke()))
         }
      }
   }
}

@ExperimentalKotest
suspend fun TestContext.maybeLog(message: LogFn, level: LogLevel) {
   if (level >= configuration.logLevel) {
      logs?.apply {
         add(LogEntry(level, message.invoke()))
      }
   }
}

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Trace].
 */
@ExperimentalKotest
suspend fun TestContext.trace(message:  LogFn) = maybeLog(message, LogLevel.Trace)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Trace]
 */
@ExperimentalKotest
suspend fun TestLogger.trace(message: LogFn) = maybeLog(message, LogLevel.Trace)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Debug].
 */
@ExperimentalKotest
suspend fun TestContext.debug(message:  LogFn) = maybeLog(message, LogLevel.Debug)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Debug]
 */
@ExperimentalKotest
suspend fun TestLogger.debug(message: LogFn) = maybeLog(message, LogLevel.Debug)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Info] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.info(message:  LogFn) = maybeLog(message, LogLevel.Info)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Info]
 */
@ExperimentalKotest
suspend fun TestLogger.info(message: LogFn) = maybeLog(message, LogLevel.Info)

/**
 * Appends to the [TestContext] log, when the log level is [io.kotest.core.config.LogLevel.Warn] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.warn(message:  LogFn) = maybeLog(message, LogLevel.Warn)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Warn]
 */
@ExperimentalKotest
suspend fun TestLogger.warn(message: LogFn) = maybeLog(message, LogLevel.Warn)

/**
 * Appends to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Error] or higher.
 */
@ExperimentalKotest
suspend fun TestContext.error(message:  LogFn) = maybeLog(message, LogLevel.Error)

/**
 * Appends to the [TestLogger] reference to the [TestContext] log, when the log level is set to [io.kotest.core.config.LogLevel.Error]
 */
@ExperimentalKotest
suspend fun TestLogger.error(message: LogFn) = maybeLog(message, LogLevel.Error)

