package io.kotest.engine.test.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.listeners.Listener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@ExperimentalKotest
interface LogListener : Listener {
   suspend fun consume(testCase: TestCase, logs: List<Any>)
}

@ExperimentalKotest
object ConsoleLogListener : LogListener {
   override val name = "ConsoleLogListener"
   override suspend fun consume(testCase: TestCase, logs: List<Any>) {
      println(" - ${testCase.description}")
      logs.forEach { println(it) }
   }
}

@ExperimentalKotest
private class TestContextLoggingCoroutineContextElement(val logs: MutableList<Any>) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestContextLoggingCoroutineContextElement>
}

@OptIn(ExperimentalKotest::class)
internal object CoroutineLoggingTestExecutionFilter : TestExecutionFilter {
   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      val loggingListeners = configuration.listeners().filterIsInstance<LogListener>().map { it to Mutex() }

      if (configuration.logLevel.isDisabled() || loggingListeners.isEmpty()) {
         test(testCase, context)
      } else {
         val contextWithLogging = context.withCoroutineContext(TestContextLoggingCoroutineContextElement(mutableListOf()))

         try {
            test(testCase, contextWithLogging)
         } catch (ex: Exception) {
            throw ex
         } finally {
            val logs = contextWithLogging.getLogs()
            loggingListeners.forEach { (listener, mut) -> mut.withLock { runCatching { listener.consume(testCase, logs) } } }
         }
      }
   }
}

@ExperimentalKotest
private fun TestContext.getLogs() = this.coroutineContext[TestContextLoggingCoroutineContextElement]?.logs?.toList() ?: listOf()

@ExperimentalKotest
suspend fun CoroutineScope.maybeLog(enabled: Boolean, message: suspend () -> Any?) {
   if (!enabled) return
   val logs = this.coroutineContext[TestContextLoggingCoroutineContextElement]?.logs ?: return
   var result = message() ?: return

   logs.add(result)
}

@ExperimentalKotest
suspend fun TestContext.debug(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isDebugEnabled(), message)

@ExperimentalKotest
suspend fun TestContext.info(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isInfoEnabled(), message)

@ExperimentalKotest
suspend fun TestContext.warn(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isWarnEnabled(), message)

@ExperimentalKotest
suspend fun TestContext.error(message: suspend () -> Any?) = maybeLog(configuration.logLevel.isErrorEnabled(), message)
