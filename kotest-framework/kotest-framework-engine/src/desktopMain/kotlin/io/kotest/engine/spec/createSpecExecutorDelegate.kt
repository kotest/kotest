package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.registration.DuplicateNameHandlingRegistration
import io.kotest.engine.test.registration.InOrderRegistration
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.defaultTestScope
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName

@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   configuration: Configuration,
): SpecExecutorDelegate =
   DefaultSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory, configuration)

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
@ExperimentalKotest
internal class DefaultSpecExecutorDelegate(
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration
) : SpecExecutorDelegate {

   private val materializer = Materializer(configuration)
   private val logger = Logger(this::class)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      logger.log { Pair(spec::class.bestName(), "Executing spec $spec") }
      materializer.materialize(spec)
         .forEach { testCase ->
            logger.log { Pair(spec::class.bestName(), "Executing testCase $testCase") }
            TestCaseExecutor(
               TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
               coroutineDispatcherFactory,
               configuration,
               DuplicateNameHandlingRegistration(
                  configuration.duplicateTestNameMode,
                  InOrderRegistration(testCase, listener, coroutineDispatcherFactory, configuration)
               )
            ).execute(testCase, defaultTestScope(testCase))
         }
      return emptyMap()
   }
}
