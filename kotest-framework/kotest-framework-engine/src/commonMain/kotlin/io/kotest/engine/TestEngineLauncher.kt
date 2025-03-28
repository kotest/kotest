@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.kotest.engine

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.tags.TagExpression
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via the [TestEngine].
 *
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 *
 * @param platform specifies the platform which the tests will be running on.
 */
class TestEngineLauncher(
   private val platform: Platform,
   private val listener: TestEngineListener,
   private val config: AbstractProjectConfig?,
   private val refs: List<SpecRef>,
   private val tagExpression: TagExpression?,
   private val registry: ExtensionRegistry,
) {

   private val logger = Logger(TestEngineLauncher::class)

   constructor() : this(NoopTestEngineListener)

   constructor(listener: TestEngineListener) : this(
      Platform.JVM,
      listener,
      null,
      emptyList(),
      null,
      DefaultExtensionRegistry(),
   )

   /**
    * Convenience function to be called by the compiler plugin to set up the TeamCity listener.
    */
   fun withTeamCityListener(): TestEngineLauncher {
      return withListener(TeamCityTestEngineListener())
   }

   /**
    * Sets the [TestEngineListener] to be notified of [TestEngine] events.
    */
   fun withListener(listener: TestEngineListener): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = tagExpression,
         registry = registry,
      )
   }

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = specs.toList().map { SpecRef.Singleton(it) },
         tagExpression = tagExpression,
         registry = registry,
      )
   }

   fun withClasses(vararg specs: KClass<out Spec>): TestEngineLauncher = withClasses(specs.toList())
   fun withClasses(specs: List<KClass<out Spec>>): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = specs.toList().map { SpecRef.Reference(it) },
         tagExpression = tagExpression,
         registry = registry,
      )
   }

   /**
    * Sets a [AbstractProjectConfig] that was detected by the compiler plugin or loaded programmatically.
    */
   fun withProjectConfig(config: AbstractProjectConfig?): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = tagExpression,
         registry = registry,
      )
   }

//   /**
//    * Sets an [AbstractProjectConfig] that was detected by reflection on the JVM.
//    * This is a no-op on non-JVM platforms.
//    */
//   @JVMOnly
//   fun withReflectionProjectConfig(): TestEngineLauncher {
//      val config = loadProjectConfigFromReflection()
//      return withProjectConfig(config)
//   }

   fun withTagExpression(expression: TagExpression?): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = expression,
         registry = registry,
      )
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

   fun withJs(): TestEngineLauncher = withPlatform(Platform.JS)
   fun withWasmJs(): TestEngineLauncher = withPlatform(Platform.WasmJs).withTeamCityListener()
   fun withNative(): TestEngineLauncher = withPlatform(Platform.Native)
   fun withJvm(): TestEngineLauncher = withPlatform(Platform.JVM)

   fun withPlatform(platform: Platform): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = tagExpression,
         registry = registry,
      )
   }

   private fun toConfig(): TestEngineConfig {

      // if the engine was configured with explicit tags, we register those via a tag extension
      tagExpression?.let { registry.add(SpecifiedTagsTagExtension(it)) }

      return TestEngineConfig(
         listener = ThreadSafeTestEngineListener(
            PinnedSpecTestEngineListener(
               listener
            )
         ),
         interceptors = testEngineInterceptors(),
         projectConfig = config,
         tagExpression,
         platform,
         registry,
      )
   }

   /**
    * Launch the [TestEngine] in an existing coroutine without blocking.
    */
   suspend fun async(): EngineResult {
      logger.log { "Launching Test Engine" }
      val engine = TestEngine(toConfig())
      return engine.execute(TestSuite(refs))
   }

   /**
    * Launch the [TestEngine] created from this builder and block the thread until execution has completed.
    * This method will throw on JS.
    */
   fun launch(): EngineResult {
      logger.log { "Launching Test Engine" }
      return runBlocking {
         val engine = TestEngine(toConfig())
         engine.execute(TestSuite(refs))
      }
   }

   /**
    * Launch the [TestEngine] created from this builder using a Javascript promise.
    * This method will throw on JVM or native.
    */
   fun promise() {
      logger.log { "Launching Test Engine in Javascript promise" }
      runPromise {
         val engine = TestEngine(toConfig())
         engine.execute(TestSuite(refs))
      }
   }
}
