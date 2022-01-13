package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
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
   suspend fun intercept(context: EngineContext, execute: suspend (EngineContext) -> EngineResult): EngineResult
}

data class EngineContext(
   val suite: TestSuite,
   val listener: TestEngineListener,
   val tags: TagExpression,
   val configuration: ProjectConfiguration,
) {

   constructor(configuration: ProjectConfiguration) : this(
      TestSuite.empty,
      NoopTestEngineListener,
      TagExpression.Empty,
      configuration
   )

   companion object {
      val empty = EngineContext(TestSuite.empty, NoopTestEngineListener, TagExpression.Empty, ProjectConfiguration())
   }

   /**
    * Returns this [EngineContext] with the given [listener] added via a [CompositeTestEngineListener].
    */
   fun mergeListener(listener: TestEngineListener): EngineContext {
      return EngineContext(suite, CompositeTestEngineListener(listOf(this.listener, listener)), tags, configuration)
   }

   fun withTestSuite(suite: TestSuite): EngineContext {
      return EngineContext(suite, listener, tags, configuration)
   }

   fun withListener(listener: TestEngineListener): EngineContext {
      return EngineContext(suite, listener, tags, configuration)
   }

   fun withConfiguration(c: ProjectConfiguration): EngineContext {
      return EngineContext(suite, listener, tags, c)
   }

   fun withTags(tags: TagExpression): EngineContext {
      return EngineContext(suite, listener, tags, configuration)
   }
}

fun ProjectContext.toEngineContext(context: EngineContext): EngineContext {
   return EngineContext(
      suite,
      context.listener,
      tags,
      configuration
   )
}

fun EngineContext.toProjectContext(): ProjectContext {
   return ProjectContext(
      suite,
      tags,
      configuration
   )
}
