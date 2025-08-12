package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.common.Platform
import io.kotest.core.Logger
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.TestSuite
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.NextEngineInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.tags.TagExpression

data class EngineResult(
   val errors: List<Throwable>, // these are errors during engine processing, not test failures
   val testFailures: Boolean,
) {

   companion object {
      val empty = EngineResult(emptyList(), false)
   }

   fun addError(t: Throwable): EngineResult {
      return copy(errors = this.errors + t)
   }

   fun addErrors(errors: List<ExtensionException.AfterProjectException>): EngineResult {
      return copy(errors = this.errors + errors)
   }

   fun withTestFailures(): EngineResult {
      return copy(testFailures = true)
   }
}

@KotestInternal
data class TestEngineConfig(
   val listener: TestEngineListener,
   val interceptors: List<EngineInterceptor>,
   val projectConfig: AbstractProjectConfig?,
   val explicitTags: TagExpression?,
   val platform: Platform,
   val registry: ExtensionRegistry,
)

/**
 * Multiplatform Kotest Test Engine.
 */
@KotestInternal
class TestEngine(private val config: TestEngineConfig) {

   private val logger = Logger(this::class)

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineInterceptor]s.
    */
   internal suspend fun execute(suite: TestSuite): EngineResult {
      logger.log { Pair(null, "Initiating test suite with ${suite.specs.size} specs") }
      logger.log { Pair(null, "${config.interceptors.size} engine interceptors") }

      val innerExecute = NextEngineInterceptor { context ->
         TestSuiteScheduler(context).schedule(context.suite)
      }

      val execute = config.interceptors.foldRight(innerExecute) { extension, next ->
         NextEngineInterceptor { context -> extension.intercept(context, next) }
      }

      val tags = TagExpression.Empty // todo = config.configuration.runtimeTagExpression()
      logger.log { Pair(null, "TestEngine: Active tags: ${tags.expression}") }

      return execute(
         EngineContext(
            suite = suite,
            listener = config.listener,
            tags = tags,
            projectConfig = config.projectConfig,
            platform = config.platform,
            registry = config.registry,
         )
      )
   }
}

