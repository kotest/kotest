package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.Platform
import io.kotest.engine.tags.TagExpression
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
import io.kotest.engine.config.ProjectConfigResolver
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

/**
 * Internal state used by the engine.
 */
@KotestInternal
data class EngineContext(
  val suite: TestSuite,
  val listener: TestEngineListener,
  val tags: TagExpression,
  val projectConfig: AbstractProjectConfig?,
  val projectConfigResolver: ProjectConfigResolver,
  val platform: Platform,
  val state: MutableMap<String, Any>, // mutable map that can be used for storing state during the engine execution
) {

   constructor(projectConfig: AbstractProjectConfig, platform: Platform) : this(
      TestSuite.empty,
      NoopTestEngineListener,
      TagExpression.Empty,
      projectConfig,
      ProjectConfigResolver(projectConfig),
      platform,
      mutableMapOf(),
   )

   companion object {
      val empty = EngineContext(
         TestSuite.empty,
         NoopTestEngineListener,
         TagExpression.Empty,
         null,
         ProjectConfigResolver(null),
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
         projectConfig,
         projectConfigResolver,
         platform,
         state,
      )
   }

   fun withTestSuite(suite: TestSuite): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         projectConfig,
         projectConfigResolver,
         platform,
         state,
      )
   }

   fun withListener(listener: TestEngineListener): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         projectConfig,
         projectConfigResolver,
         platform,
         state,
      )
   }

   fun withConfiguration(projectConfig: AbstractProjectConfig?): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         projectConfig,
         projectConfigResolver,
         platform,
         state,
      )
   }

   fun withTags(tags: TagExpression): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         projectConfig,
         projectConfigResolver,
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
      projectConfig,
      ProjectConfigResolver(projectConfig),
      platform,
      state,
   )
}

internal fun EngineContext.toProjectContext(): ProjectContext {
   return ProjectContext(
      suite,
      tags,
      projectConfig,
   )
}
