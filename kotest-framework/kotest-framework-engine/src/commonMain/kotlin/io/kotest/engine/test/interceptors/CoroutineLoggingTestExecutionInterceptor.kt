package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.listeners.Listener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * On completion of the execution of a given testCase,
 * the values logged by way of [debug], [info], [warn], and [error] will be passed to afterEach.
 *
 * Listeners can use testId on [TestCase.description] to cross-reference the [TestResult] with the provided logs.
 */
@ExperimentalKotest
interface LogListener : Listener {
   suspend fun afterEach(testCase: TestCase, logs: List<Any>)
}

/**
 * [SerialLogListener] wraps the user provided [LogListener] with a mutex,
 * so we can guarantee that calls to [LogListener.afterEach] aren't interleaved.
 */
@OptIn(ExperimentalKotest::class)
internal class SerialLogListener constructor(private val logListener: LogListener) {
   private val mutex = Mutex()

   suspend fun afterEach(testCase: TestCase, logs: List<Any>) = mutex.withLock {
      runCatching {
         logListener.afterEach(testCase, logs)
      }
   }
}

@ExperimentalKotest
object ConsoleLogListener : LogListener {
   override val name = "ConsoleLogListener"
   override suspend fun afterEach(testCase: TestCase, logs: List<Any>) {
      println(" - ${testCase.description}")
      logs.forEach { println(it) }
   }
}

@ExperimentalKotest
private class TestContextLoggingCoroutineContextElement(val logs: MutableList<Any>) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestContextLoggingCoroutineContextElement>
}

@OptIn(ExperimentalKotest::class)
internal object CoroutineLoggingTestExecutionInterceptor : TestExecutionInterceptor {
   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      when {
         configuration.logLevel.isDisabled() || listeners.isEmpty() -> test(testCase, context)
         else -> {
            val contextWithLogging = context.withCoroutineContext(TestContextLoggingCoroutineContextElement(mutableListOf()))

            try {
               test(testCase, contextWithLogging)
            } catch (ex: Exception) {
               throw ex
            } finally {
               val logs = contextWithLogging.getLogs()
               listeners.forEach { it.afterEach(testCase, logs) }
            }
         }
      }
   }
}

@OptIn(ExperimentalKotest::class)
private fun TestContext.getLogs() = this.coroutineContext[TestContextLoggingCoroutineContextElement]?.logs?.toList() ?: listOf()

@ExperimentalKotest
private suspend fun TestContext.maybeLog(enabled: Boolean, message: suspend () -> Any?) {
   if (!enabled) return
   val logs = this.coroutineContext[TestContextLoggingCoroutineContextElement]?.logs ?: return
   val result = message() ?: return

   logs.add(result)
}

/**
 * Adds a log to the [TestContext], when the log level is set to [io.kotest.core.config.LogLevel.DEBUG].
 */
@ExperimentalKotest
suspend fun TestContext.debug(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isDebugEnabled(), message)

/**
 * Adds a log to the [TestContext], when the log level is set to [io.kotest.core.config.LogLevel.INFO].
 */
@ExperimentalKotest
suspend fun TestContext.info(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isInfoEnabled(), message)

/**
 * Adds a log to the [TestContext], when the log level is set to [io.kotest.core.config.LogLevel.WARN].
 */
@ExperimentalKotest
suspend fun TestContext.warn(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isWarnEnabled(), message)

/**
 * Adds a log to the [TestContext] when the log level is set to [io.kotest.core.config.LogLevel.ERROR].
 */
@ExperimentalKotest
suspend fun TestContext.error(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isErrorEnabled(), message)
