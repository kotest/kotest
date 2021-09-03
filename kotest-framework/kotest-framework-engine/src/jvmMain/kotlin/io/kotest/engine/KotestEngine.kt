package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.detectAbstractProjectConfigs
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.extensions.TestSuiteSchedulerExtension
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.DefaultTestSuiteScheduler
import io.kotest.engine.spec.SpecExecutor
import io.kotest.fp.Try
import io.kotest.mpp.log

data class KotestEngineConfig(
   val testFilters: List<TestFilter>,
   val specFilters: List<SpecFilter>,
   val listener: TestEngineListener,
   val explicitTags: Tags?,
   val dumpConfig: Boolean,
)

class KotestEngine(private val config: KotestEngineConfig) {

   init {

      ConfigManager.initialize(configuration, detectAbstractProjectConfigs())

      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.explicitTags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.testFilters)
   }

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineInterceptor]s.
    */
   suspend fun execute(suite: TestSuite): EngineResult {

      val innerExecute: suspend (TestSuite, TestEngineListener) -> EngineResult =
         { ts, tel ->
            val error = submitAll(ts, tel).errorOrNull()
            EngineResult(listOfNotNull(error))
         }

      val execute = testEngineInterceptors(configuration).foldRight(innerExecute) { extension, next ->
         { ts, tel -> extension.intercept(ts, tel, next) }
      }

      return execute.invoke(suite, config.listener)
   }

   fun cleanup() {
      configuration.deregisterFilters(config.testFilters)
   }

   /**
    * Submit the test suite to be executed. Returns a success if everything completed normally,
    * or returns a failure if an unexpected (not test failure) error occured.
    */
   private suspend fun submitAll(suite: TestSuite, listener: TestEngineListener): Try<Unit> = Try {
      log { "KotestEngine: Beginning test plan [specs=${suite.classes.size}, scripts=0, parallelism=${configuration.parallelism}}]" }

      // scripts always run sequentially
//         log { "KotestEngine: Launching ${suite.scripts.size} scripts" }
//         if (suite.scripts.isNotEmpty()) {
//            suite.scripts.forEach { scriptKClass ->
//               log { scriptKClass.java.methods.toList().toString() }
//               ScriptExecutor(listener)
//                  .execute(scriptKClass)
//            }
//            log { "KotestEngine: Script execution completed" }
//         }

      val executor = SpecExecutor(listener)
      log { "KotestEngine: Will use spec executor $executor" }

      val scheduler = configuration.extensions()
         .filterIsInstance<TestSuiteSchedulerExtension>()
         .firstOrNull()?.scheduler()
         ?: DefaultTestSuiteScheduler(configuration.concurrentSpecs ?: configuration.parallelism)

      log { "KotestEngine: Will use scheduler $scheduler" }
      scheduler.schedule(suite, { }, { executor.execute(it) })
   }
}
