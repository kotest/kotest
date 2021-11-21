package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.common.flatMap
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.registration.InOrderRegistration
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
@ExperimentalKotest
internal class SingleInstanceSpecRunner(
   listener: TestEngineListener,
   scheduler: TestScheduler,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   configuration: Configuration,
) : SpecRunner(listener, scheduler, configuration) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()
   private val extensions = SpecExtensions(configuration.registry())
   private val logger = Logger(SingleInstanceSpecRunner::class)
   private val testCaseListener = TestCaseExecutionListenerToTestEngineListenerAdapter(listener)
   private val registration = InOrderRegistration(testCaseListener, defaultCoroutineDispatcherFactory, configuration)
   private val testExecutor = TestCaseExecutor(
      testCaseListener,
      defaultCoroutineDispatcherFactory,
      configuration,
      registration,
   )

   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "executing spec $spec") }

      suspend fun interceptAndRun() = runCatching {
         launch(spec) {
            logger.log { Pair(it.name.testName, "Executing test $it") }
            runTest(it)
         }
      }

      try {
         return coroutineScope {
            extensions.beforeSpec(spec)
               .flatMap { interceptAndRun() }
               .flatMap { extensions.afterSpec(spec) }
               .map { results }
         }
      } catch (e: Exception) {
         e.printStackTrace()
         throw e
      }
   }

   private suspend fun runTest(testCase: TestCase): TestResult {
      val result = testExecutor.execute(testCase)
      results[testCase] = result
      return result
   }
}
