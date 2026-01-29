@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.kotest.engine

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecRef
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.filter.IncludePatternEnvDescriptorFilter
import io.kotest.engine.extensions.tags.SpecifiedTagsTagExtension
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.tags.TagExpression

/**
 * A builder class for creating and executing tests via a [TestEngine].
 *
 * This API is considered internal and should be used by other Kotest components only.
 */
@KotestInternal
data class TestEngineLauncher(
   private val listeners: List<TestEngineListener>,
   private val config: AbstractProjectConfig?,
   private val refs: List<SpecRef>,
   private val tagExpression: TagExpression?,
   private val registry: ExtensionRegistry,
) {

   // we use this to capture any test failures so we know to exit appropriately
   private val collecting = CollectingTestEngineListener()
   private val logger = Logger(TestEngineLauncher::class)

   constructor() : this(
      listOf(),
      null,
      emptyList(),
      null,
      DefaultExtensionRegistry(),
   )

   /**
    * Convenience function to add a [TeamCityTestEngineListener].
    * Returns a copy of this launcher with the listener added.
    */
   fun withTeamCityListener(): TestEngineLauncher {
      return withListener(TeamCityTestEngineListener())
   }

   /**
    * Convenience function to add a [ConsoleTestEngineListener].
    * Returns a copy of this launcher with the listener added.
    */
   fun withConsoleListener(): TestEngineLauncher {
      return withListener(ConsoleTestEngineListener())
   }

   /**
    * Adds the [TestEngineListener] to be notified of [TestEngine] events.
    * Returns a copy of this launcher with the given [TestEngineListener] added.
    */
   fun withListener(listener: TestEngineListener?): TestEngineLauncher {
      return if (listener == null) this else copy(listeners = this.listeners + listener)
   }

   fun withListeners(listeners: Collection<TestEngineListener>): TestEngineLauncher {
      return if (listeners.isEmpty()) this else copy(listeners = this.listeners + listeners)
   }

   fun withNoOpListener(): TestEngineLauncher {
      return withListener(NoopTestEngineListener)
   }

   fun withSpecRefs(vararg refs: SpecRef): TestEngineLauncher = withSpecRefs(refs.toList())
   fun withSpecRefs(refs: List<SpecRef>): TestEngineLauncher {
      return copy(refs = this.refs + refs)
   }

   /**
    * Sets a [AbstractProjectConfig] that was detected by the compiler plugin or loaded programmatically.
    *
    * This will override any existing project config.
    */
   fun withProjectConfig(config: AbstractProjectConfig?): TestEngineLauncher {
      return copy(config = config)
   }

   fun withTagExpression(expression: TagExpression?): TestEngineLauncher {
      return copy(tagExpression = expression)
   }

   /**
    * Returns a copy of this launcher with the given [extension] added to the configuration.
    */
   fun addExtension(extension: Extension): TestEngineLauncher = addExtensions(listOf(extension))

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun addExtensions(vararg extensions: Extension): TestEngineLauncher = addExtensions(extensions.toList())

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun addExtensions(extensions: List<Extension>): TestEngineLauncher {
      extensions.forEach { registry.add(it) }
      return this
   }

   private fun toConfig(): TestEngineConfig {

      val safeListener = ThreadSafeTestEngineListener( // to avoid race conditions with concurrent spec execution
         PinnedSpecTestEngineListener( // to ensure we don't interleave output in TCSM which requires sequential outputs
            CompositeTestEngineListener(listeners + collecting) // add in a collecting listener so we know to exit appropriately on errors
         )
      )

      // add in extensions that are enabled by default
      registry.add(IncludePatternEnvDescriptorFilter)

      // if the engine was configured with explicit tags, we register those via a tag extension
      tagExpression?.let { registry.add(SpecifiedTagsTagExtension(it)) }

      return TestEngineConfig(
         listener = safeListener,
         projectConfig = config,
         tagExpression,
         registry,
      )
   }

   /**
    * Launch the [TestEngine] in an existing coroutine without blocking.
    *
    * @return the [EngineResult] containing the results of the test execution.
    */
   suspend fun execute(): EngineResult {
      logger.log { "Launching Test Engine" }
      val engine = TestEngine(toConfig())
      return engine.execute(TestSuite(refs))
   }
}
