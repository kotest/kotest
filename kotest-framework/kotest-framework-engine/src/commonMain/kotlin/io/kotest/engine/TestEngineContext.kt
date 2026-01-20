package io.kotest.engine

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.ProjectExtensions
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.TestExtensions

/**
 * Internal state used by the [TestEngine] during execution.
 */
@ConsistentCopyVisibility
internal data class TestEngineContext internal constructor(
   val suite: TestSuite,
   val tags: TagExpression,
   val registry: ExtensionRegistry,
   val projectConfig: AbstractProjectConfig?,
   val listener: CompositeTestEngineListener, // the combined listener
   val collector: CollectingTestEngineListener, // used to collect failures during execution
) {

   internal val projectConfigResolver: ProjectConfigResolver = ProjectConfigResolver(projectConfig, registry)
   internal val specConfigResolver: SpecConfigResolver = SpecConfigResolver(projectConfig, registry)
   internal val testConfigResolver: TestConfigResolver = TestConfigResolver(projectConfig, registry)

   internal fun specExtensions() = SpecExtensions(specConfigResolver, projectConfigResolver)
   internal fun testExtensions() = TestExtensions(testConfigResolver)
   internal fun projectExtensions() = ProjectExtensions(projectConfigResolver)

   companion object {

      fun create(
         suite: TestSuite,
         tags: TagExpression,
         registry: ExtensionRegistry,
         projectConfig: AbstractProjectConfig?,
         listener: TestEngineListener,
      ): TestEngineContext {
         val collector = CollectingTestEngineListener()
         return TestEngineContext(
            suite = suite,
            tags = tags,
            registry = registry,
            projectConfig = projectConfig,
            listener = CompositeTestEngineListener(listener, collector),
            collector = collector
         )
      }

      private val registry = DefaultExtensionRegistry()
      val empty = create(
         suite = TestSuite.empty,
         tags = TagExpression.Empty,
         registry = registry,
         projectConfig = null,
         listener = NoopTestEngineListener,
      )
   }
}

/**
 * Converts a [ProjectContext] into a [TestEngineContext] using updated values
 * from the project context, combined with the non public values from the engine context.
 */
internal fun ProjectContext.toEngineContext(
   context: TestEngineContext,
): TestEngineContext {
   return TestEngineContext(
      suite = suite,
      tags = tags,
      projectConfig = projectConfig,
      listener = context.listener,
      collector = context.collector,
      registry = context.registry,
   )
}

/**
 * Creates a public [ProjectContext] from the internal [TestEngineContext].
 */
internal fun TestEngineContext.toProjectContext(): ProjectContext {
   return ProjectContext(
      suite,
      tags,
      projectConfig,
   )
}
