package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.Platform
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.TestExtensions

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
   val registry: ExtensionRegistry,
   val projectConfig: AbstractProjectConfig?,
   val projectConfigResolver: ProjectConfigResolver,
   val specConfigResolver: SpecConfigResolver,
   val testConfigResolver: TestConfigResolver,
   val platform: Platform,
   val state: MutableMap<String, Any>, // mutable map that can be used for storing state during the engine execution
) {

   internal fun specExtensions() = SpecExtensions(specConfigResolver, projectConfigResolver)
   internal fun testExtensions() = TestExtensions(testConfigResolver)

   companion object {

      operator fun invoke(projectConfig: AbstractProjectConfig, platform: Platform): EngineContext {
         val registry = DefaultExtensionRegistry()
         return EngineContext(
            suite = TestSuite.empty,
            listener = NoopTestEngineListener,
            tags = TagExpression.Empty,
            registry = registry,
            projectConfig = projectConfig,
            specConfigResolver = SpecConfigResolver(projectConfig, registry),
            testConfigResolver = TestConfigResolver(projectConfig, registry),
            projectConfigResolver = ProjectConfigResolver(projectConfig, registry),
            platform = platform,
            state = mutableMapOf(),
         )
      }

      private val registry = DefaultExtensionRegistry()
      val empty = EngineContext(
         suite = TestSuite.empty,
         listener = NoopTestEngineListener,
         tags = TagExpression.Empty,
         registry = registry,
         projectConfig = null,
         specConfigResolver = SpecConfigResolver(null, registry),
         testConfigResolver = TestConfigResolver(null, registry),
         projectConfigResolver = ProjectConfigResolver(null, registry),
         platform = Platform.JVM,
         state = mutableMapOf(),
      )
   }

   /**
    * Returns this [EngineContext] with the given [listener] added via a [CompositeTestEngineListener].
    */
   fun mergeListener(listener: TestEngineListener): EngineContext {
      return EngineContext(
         suite = suite,
         listener = CompositeTestEngineListener(listOf(this.listener, listener)),
         tags = tags,
         registry = registry,
         projectConfig = projectConfig,
         projectConfigResolver = projectConfigResolver,
         specConfigResolver = specConfigResolver,
         testConfigResolver = testConfigResolver,
         platform = platform,
         state = state,
      )
   }

   fun withTestSuite(suite: TestSuite): EngineContext {
      return EngineContext(
         suite = suite,
         listener = listener,
         tags = tags,
         registry = registry,
         projectConfig = projectConfig,
         projectConfigResolver = projectConfigResolver,
         specConfigResolver = specConfigResolver,
         testConfigResolver = testConfigResolver,
         platform = platform,
         state = state,
      )
   }

   fun withListener(listener: TestEngineListener): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         registry,
         projectConfig,
         projectConfigResolver = projectConfigResolver,
         specConfigResolver = specConfigResolver,
         testConfigResolver = testConfigResolver,
         platform,
         state,
      )
   }

   fun withConfiguration(projectConfig: AbstractProjectConfig?): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         registry,
         projectConfig,
         projectConfigResolver = projectConfigResolver,
         specConfigResolver = specConfigResolver,
         testConfigResolver = testConfigResolver,
         platform,
         state,
      )
   }

   fun withTags(tags: TagExpression): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         registry,
         projectConfig,
         projectConfigResolver = projectConfigResolver,
         specConfigResolver = specConfigResolver,
         testConfigResolver = testConfigResolver,
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
      context.registry,
      projectConfig,
      ProjectConfigResolver(projectConfig, context.registry),
      SpecConfigResolver(projectConfig, context.registry),
      TestConfigResolver(projectConfig, context.registry),
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
