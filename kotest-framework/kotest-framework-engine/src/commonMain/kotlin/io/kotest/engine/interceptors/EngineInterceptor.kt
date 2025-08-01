package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.common.Platform
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.ProjectExtensions
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
) {

   internal fun specExtensions() = SpecExtensions(specConfigResolver, projectConfigResolver)
   internal fun testExtensions() = TestExtensions(testConfigResolver)
   internal fun projectExtensions() = ProjectExtensions(projectConfigResolver)

   companion object {

      operator fun invoke(projectConfig: AbstractProjectConfig?, platform: Platform): EngineContext {
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
         )
      }

      operator fun invoke(
         suite: TestSuite,
         listener: TestEngineListener,
         tags: TagExpression,
         projectConfig: AbstractProjectConfig?,
         platform: Platform,
         registry: ExtensionRegistry,
      ): EngineContext {
         return EngineContext(
            suite = suite,
            listener = listener,
            tags = tags,
            registry = registry,
            projectConfig = projectConfig,
            specConfigResolver = SpecConfigResolver(projectConfig, registry),
            testConfigResolver = TestConfigResolver(projectConfig, registry),
            projectConfigResolver = ProjectConfigResolver(projectConfig, registry),
            platform = platform,
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
      )
   }

   fun withProjectConfig(projectConfig: AbstractProjectConfig?): EngineContext {
      return EngineContext(
         suite,
         listener,
         tags,
         registry,
         projectConfig,
         projectConfigResolver = ProjectConfigResolver(projectConfig, registry),
         specConfigResolver = SpecConfigResolver(projectConfig, registry),
         testConfigResolver = TestConfigResolver(projectConfig, registry),
         platform,
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
      )
   }
}

internal fun ProjectContext.toEngineContext(
   context: EngineContext,
   platform: Platform,
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
   )
}

internal fun EngineContext.toProjectContext(): ProjectContext {
   return ProjectContext(
      suite,
      tags,
      projectConfig,
   )
}
