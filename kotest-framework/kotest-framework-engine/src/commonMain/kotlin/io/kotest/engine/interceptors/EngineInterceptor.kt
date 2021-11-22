package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.ProjectContext
import io.kotest.core.TagExpression
import io.kotest.core.TestSuite
import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.EngineResult
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.config.toConfiguration
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener

@KotestInternal
interface EngineInterceptor {
   suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult
}

data class EngineContext(
   val listener: TestEngineListener,
   val suite: TestSuite,
   val tags: TagExpression,
   val configuration: ProjectConfiguration,
) {

   companion object {
      val empty = EngineContext(
         NoopTestEngineListener,
         TestSuite.empty,
         TagExpression.Empty,
         MutableConfiguration().toConfiguration()
      )
   }

   /**
    * Returns a copy of this context with the [TestEngineListener] merged with the given [listener]
    * via a [CompositeTestEngineListener].
    */
   fun mergeListener(listener: TestEngineListener): EngineContext {
      return EngineContext(CompositeTestEngineListener(listOf(this.listener, listener)), suite, tags, configuration)
   }

   /**
    * Returns a copy of this context with the [TestSuite] replaced with the given [suite].
    */
   fun withTestSuite(suite: TestSuite): EngineContext {
      return EngineContext(listener, suite, tags, configuration)
   }

   /**
    * Returns a copy of this context with the [ProjectConfiguration] replaced
    * with the given [suite].
    */
   fun withProjectConfiguration(c: ProjectConfiguration): EngineContext {
      return EngineContext(listener, suite, tags, c)
   }

   /**
    * Returns a copy of this context with the [TagExpression] replaced
    * with the given [tags].
    */
   fun withTags(tags: TagExpression): EngineContext {
      return EngineContext(listener, suite, tags, configuration)
   }
}

fun ProjectContext.toEngineContext(context: EngineContext): EngineContext {
   return EngineContext(
      context.listener,
      context.suite,
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
