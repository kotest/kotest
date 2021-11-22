package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.TestSuite
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.tags.runtimeTags
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope

data class EngineResult(val errors: List<Throwable>) {

   companion object {
      val empty = EngineResult(emptyList())
   }

   fun addError(t: Throwable): EngineResult {
      return EngineResult(errors + t)
   }
}

@KotestInternal
data class TestEngineConfig(
   val listener: TestEngineListener,
   val interceptors: List<EngineInterceptor>,
   val configuration: ProjectConfiguration,
   val explicitTags: TagExpression?,
)

/**
 * Multiplatform Kotest Test Engine.
 */
@KotestInternal
class TestEngine(private val config: TestEngineConfig) {

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineInterceptor]s.
    */
   @OptIn(KotestInternal::class, ExperimentalKotest::class)
   suspend fun execute(suite: TestSuite): EngineResult {
      log { "TestEngine: Executing test suite with ${suite.specs.size} specs" }

      val innerExecute: suspend (EngineContext) -> EngineResult = { context ->
         val scheduler = when (platform) {
            Platform.JVM -> ConcurrentTestSuiteScheduler(
               config.configuration.concurrentSpecs ?: config.configuration.parallelism,
               context,
            )
            Platform.JS -> SequentialTestSuiteScheduler(context)
            Platform.Native -> SequentialTestSuiteScheduler(context)
         }
         scheduler.schedule(context.suite, context.listener)
      }

      log { "TestEngine: ${config.interceptors.size} engine interceptors:" }
      config.interceptors.forEach {
         log { "\t\t${it::class.simpleName}" }
      }

      val execute = config.interceptors.foldRight(innerExecute) { extension, next ->
         { context -> extension.intercept(context, next) }
      }

      val tags = config.configuration.runtimeTags()
      log { "TestEngine: Active tags: ${tags.expression}" }

      // we want to suspend the engine while we wait for all specs to complete
      return coroutineScope {
         execute(EngineContext(suite, config.listener, tags, config.configuration))
      }
   }
}

