package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.Platform
import io.kotest.core.TagExpression
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener

/**
 * Internal pipeline that intercepts calls to the engine.
 *
 * This can be used to execute code before or after the engine
 * and permits changing the [EngineContext].
 */
@KotestInternal
interface EngineInterceptor {
   suspend fun intercept(context: EngineContext, execute: NextEngineInterceptor): EngineResult
}

/**
 * A functional interface for the interceptor callback, to reduce the size of stack traces.
 *
 * With a normal lambda type, each call adds three lines to the stacktrace, but an interface only adds one line.
 */
@KotestInternal
fun interface NextEngineInterceptor {
   suspend operator fun invoke(context: EngineContext): EngineResult
}

@KotestInternal
data class EngineContext(
  val suite: TestSuite,
  val listener: TestEngineListener,
  val tags: TagExpression,
  val configuration: ProjectConfiguration,
  val projectConfig: AbstractProjectConfig,
  val projectConfigResolver: ProjectConfigResolver,
  val specConfigResolver: SpecConfigResolver,
  val platform: Platform,
  val state: MutableMap<String, Any>, // mutable map that can be used for storing state during the engine execution
) {

   constructor(configuration: ProjectConfiguration, platform: Platform) : this(
      TestSuite.empty,
      NoopTestEngineListener,
      TagExpression.Empty,
      configuration,
      platform,
      mutableMapOf(),
   )

   companion object {
      val empty = EngineContext(
         TestSuite.empty,
         NoopTestEngineListener,
         TagExpression.Empty,
         ProjectConfiguration(),
         Platform.JVM,
         mutableMapOf(),
      )
   }

   /**
    * Returns this [EngineContext] with the given [listener] added via a [CompositeTestEngineListener].
    */
   fun mergeListener(listener: TestEngineListener): EngineContext {
      return EngineContext(
         suite,
         CompositeTestEngineListener(listOf(this.listener, listener)),
         tags,
         configuration,
         platform,
         state,
      )
   }

   fun withTestSuite(suite: TestSuite): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         configuration,
         platform,
         state,
      )
   }

   fun withListener(listener: TestEngineListener): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         configuration,
         platform,
         state,
      )
   }

   fun withConfiguration(conf: ProjectConfiguration): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         conf,
         platform,
         state,
      )
   }

   fun withTags(tags: TagExpression): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         configuration,
         platform,
         state,
      )
   }
}

internal fun ProjectContext.toEngineContext(
   context: EngineContext,
   platform: Platform,
   state: MutableMap<String, Any>
): EngineContext {
   return EngineContext(
      suite,
      context.listener,
      tags,
      configuration,
      platform,
      state,
   )
}

internal fun EngineContext.toProjectContext(): ProjectContext {
   return ProjectContext(
      suite,
      tags,
      configuration
   )
}
