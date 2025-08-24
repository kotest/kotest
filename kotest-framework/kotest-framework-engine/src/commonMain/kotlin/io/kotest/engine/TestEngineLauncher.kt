@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.kotest.engine

import io.kotest.common.Platform
import io.kotest.core.Logger
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.tags.TagExpression
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via a [TestEngine].
 *
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 *
 * @param platform specifies the platform which the tests will be running on.
 */
data class TestEngineLauncher(
   private val platform: Platform,
   private val listeners: List<TestEngineListener>,
   private val config: AbstractProjectConfig?,
   private val refs: List<SpecRef>,
   private val tagExpression: TagExpression?,
   private val registry: ExtensionRegistry,
) {

   private val collecting = CollectingTestEngineListener()
   private val logger = Logger(TestEngineLauncher::class)

   @Deprecated("Use no arg constructor. Deprecated in 6.0")
   constructor(listener: TestEngineListener) : this(
      Platform.JVM,
      listOf(listener),
      null,
      emptyList(),
      null,
      DefaultExtensionRegistry(),
   )

   constructor() : this(
      Platform.JVM,
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
      return if (listener == null) this else copy(listeners = listeners + listener)
   }

   fun withClasses(vararg specs: KClass<out Spec>): TestEngineLauncher = withClasses(specs.toList())
   fun withClasses(specs: List<KClass<out Spec>>): TestEngineLauncher =
      withSpecRefs(specs.map { SpecRef.Reference(it) })

   fun withSpecRefs(vararg refs: SpecRef): TestEngineLauncher = withSpecRefs(refs.toList())
   fun withSpecRefs(refs: List<SpecRef>): TestEngineLauncher {
      return copy(refs = refs)
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
   fun addExtension(extension: Extension): TestEngineLauncher =
      addExtensions(listOf(extension))

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun addExtensions(vararg extensions: Extension): TestEngineLauncher =
      addExtensions(extensions.toList())

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun addExtensions(extensions: List<Extension>): TestEngineLauncher {
      extensions.forEach { registry.add(it) }
      return this
   }

   /**
    * Convenience function to be called by the compiler plugin to set up the JS platform.
    */
   fun withJs(): TestEngineLauncher = withPlatform(Platform.JS)

   /**
    * Convenience function to be called by the compiler plugin to set up the WASM platform.
    */
   fun withWasmJs(): TestEngineLauncher = withPlatform(Platform.WasmJs)

   /**
    * Convenience function to be called by the compiler plugin to set up the WASM platform.
    */
   fun withWasmWasi(): TestEngineLauncher = withPlatform(Platform.WasmWasi)

   /**
    * Convenience function to be called by the compiler plugin to set up the Native platform.
    */
   fun withNative(): TestEngineLauncher = withPlatform(Platform.Native)

   /**
    * Convenience function to be called by the compiler plugin to set up the JVM platform.
    */
   fun withJvm(): TestEngineLauncher = withPlatform(Platform.JVM)

   /**
    * Returns a copy of this launcher with the given [platform] set.
    *
    * This will override the current platform.
    */
   fun withPlatform(platform: Platform): TestEngineLauncher {
      return copy(platform = platform)
   }

   private fun toConfig(): TestEngineConfig {
      require(listeners.isNotEmpty()) { "At least one TestEngineListener must be registered" }

      // if the engine was configured with explicit tags, we register those via a tag extension
      tagExpression?.let { registry.add(SpecifiedTagsTagExtension(it)) }

      val safeListeners = (listeners + collecting).map {
         ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(it))
      }

      return TestEngineConfig(
         listener = CompositeTestEngineListener(safeListeners),
         interceptors = testEngineInterceptorsForPlatform(),
         projectConfig = config,
         tagExpression,
         platform,
         registry,
      )
   }

   /**
    * Launch the [TestEngine] in an existing coroutine without blocking.
    *
    * @return the [EngineResult] containing the results of the test execution.
    */
   suspend fun async(): EngineResult {
      logger.log { "Launching Test Engine" }
      val engine = TestEngine(toConfig())
      return engine.execute(TestSuite(refs)).copy(testFailures = collecting.errors)
   }

   /**
    * Launch the [TestEngine] created from this builder and block the thread until execution has completed.
    * This method will throw on JS.
    *
    * @return the [EngineResult] containing the results of the test execution.
    */
   fun launch(): EngineResult {
      logger.log { "Launching Test Engine" }
      return runBlocking {
         val engine = TestEngine(toConfig())
         engine.execute(TestSuite(refs)).copy(testFailures = collecting.errors)
      }
   }

   /**
    * Launch the [TestEngine] created from this builder using a Javascript promise.
    * This method will throw on JVM or native.
    *
    * @return the promise that will resolve to an [EngineResult] when the tests have completed.
    */
   fun promise(): Any { // will be a Promise<EngineResult> on JS, but an error on other platforms
      logger.log { "Launching Test Engine in Javascript promise" }
      return runPromise {
         val engine = TestEngine(toConfig())
         engine.execute(TestSuite(refs)).copy(testFailures = collecting.errors)
      }
   }
}
