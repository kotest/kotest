@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.kotest.engine

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.engine.tags.TagExpression
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
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
) {

   private val logger = Logger(TestEngineLauncher::class)

   constructor() : this(
      Platform.JVM,
      NoopTestEngineListener,
      null,
      emptyList(),
      null,
   )

   constructor(listener: TestEngineListener) : this(
      Platform.JVM,
      listener,
      null,
      emptyList(),
      null,
   )

   /**
    * Convenience function to be called by the native code gen to set up the TeamCity listener.
    */
   fun withTeamCityListener(): TestEngineLauncher {
      return withListener(TeamCityTestEngineListener())
   }

   /**
    * Replace the listener with the given value.
    */
   fun withListener(listener: TestEngineListener): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = tagExpression,
      )
   }

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = specs.toList().map { SpecRef.Singleton(it) },
         tagExpression = tagExpression,
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
      )
   }

   /**
    * Sets a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   fun withProjectConfig(projectConfig: AbstractProjectConfig?): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = projectConfig,
         refs = refs,
         tagExpression = tagExpression,
      )
   }

   fun withTagExpression(expression: TagExpression?): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = expression,
      )
   }

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun withExtensions(vararg extensions: Extension): TestEngineLauncher = withExtensions(extensions.toList())

   /**
    * Returns a copy of this launcher with the given [extensions] added to the configuration.
    */
   fun withExtensions(extensions: List<Extension>): TestEngineLauncher {
      TODO()
//      extensions.forEach { projectConfiguration.registry.add(it) }
      return this
   }

   fun withConfiguration(configuration: AbstractProjectConfig?): TestEngineLauncher {
      return TestEngineLauncher(
         platform = platform,
         listener = listener,
         config = config,
         refs = refs,
         tagExpression = tagExpression,
      )
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
      )
   }

   fun toConfig(): TestEngineConfig {
      TODO()
//      // if the engine was configured with explicit tags, we register those via a tag extension
//      tagExpression?.let { projectConfiguration.registry.add(SpecifiedTagsTagExtension(it)) }
//
//      val configuration = if (configurationIsInitialized) projectConfiguration else {
//         ConfigManager.initialize(projectConfiguration) {
//            config + loadProjectConfigFromClassname()
//         }
//      }
//
//      return TestEngineConfig(
//         listener = ThreadSafeTestEngineListener(
//            PinnedSpecTestEngineListener(
//               listener
//            )
//         ),
//         interceptors = testEngineInterceptors(),
//         configuration = configuration,
//         tagExpression,
//         platform,
//      )
   }

   fun testSuite(): TestSuite = TestSuite(refs)

   /**
    * Launch the [TestEngine] in an existing coroutine without blocking.
    */
   suspend fun async(): EngineResult {
      logger.log { "Launching Test Engine" }
      val engine = TestEngine(toConfig())
      return engine.execute(testSuite())
   }

   /**
    * Launch the [TestEngine] created from this builder and block the thread until execution has completed.
    * This method will throw on JS.
    */
   fun launch(): EngineResult {
      logger.log { "Launching Test Engine" }
      return runBlocking {
         val engine = TestEngine(toConfig())
         engine.execute(testSuite())
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
         engine.execute(testSuite())
      }
   }
}
