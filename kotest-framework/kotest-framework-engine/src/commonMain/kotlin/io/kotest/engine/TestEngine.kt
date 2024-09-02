package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.core.platform
import io.kotest.core.Platform
import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.TestSuite
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.tags.runtimeTagExpression
import io.kotest.core.Logger
import io.kotest.engine.interceptors.NextEngineInterceptor

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
   val platform: Platform,
)

/**
 * Multiplatform Kotest Test Engine.
 */
@KotestInternal
class TestEngine(private val config: TestEngineConfig) {

   private val logger = Logger(this::class)

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineInterceptor]s.
    *
    * It is recommended that this method is not invoked, but instead the engine
    * is launched via the [TestEngineLauncher].
    */
   @OptIn(KotestInternal::class, ExperimentalKotest::class)
   internal suspend fun execute(suite: TestSuite): EngineResult {
      logger.log { Pair(null, "Executing test suite with ${suite.specs.size} specs") }

      val innerExecute = NextEngineInterceptor { context ->
         val scheduler = when (platform) {
            Platform.JVM -> ConcurrentTestSuiteScheduler(
               config.configuration.concurrentSpecs ?: config.configuration.parallelism,
               context,
            )

            Platform.JS,
            Platform.Native,
            Platform.WasmJs -> SequentialTestSuiteScheduler(context)
         }
         scheduler.schedule(context.suite)
      }

      logger.log { Pair(null, "${config.interceptors.size} engine interceptors") }

      val execute = config.interceptors.foldRight(innerExecute) { extension, next ->
         NextEngineInterceptor { context -> extension.intercept(context, next) }
      }

      val tags = config.configuration.runtimeTagExpression()
      logger.log { Pair(null, "TestEngine: Active tags: ${tags.expression}") }

      return execute(EngineContext(suite, config.listener, tags, config.configuration, config.platform, mutableMapOf()))
   }
}

