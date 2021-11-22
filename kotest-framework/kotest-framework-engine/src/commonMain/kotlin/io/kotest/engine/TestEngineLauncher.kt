@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.kotest.engine

import io.kotest.common.runBlocking
import io.kotest.common.runPromise
import io.kotest.core.TagExpression
import io.kotest.core.TestSuite
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.config.detectAbstractProjectConfigs
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.spec.InstanceSpecRef
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via the [TestEngine].
 *
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 */
class TestEngineLauncher(
   private val listener: TestEngineListener,
   private val configuration: MutableConfiguration,
   private val configs: List<AbstractProjectConfig>,
   private val refs: List<SpecRef>,
   private val tagExpression: TagExpression?,
   private val extensions: List<Extension>,
) {

   constructor() : this(
      NoopTestEngineListener,
      MutableConfiguration(),
      emptyList(),
      emptyList(),
      null,
      emptyList(),
   )

   constructor(listener: TestEngineListener) : this(
      listener,
      MutableConfiguration(),
      emptyList(),
      emptyList(),
      null,
      emptyList(),
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
         listener = listener,
         configuration = configuration,
         configs = configs,
         refs = refs,
         tagExpression = tagExpression,
         extensions = extensions,
      )
   }

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configuration = configuration,
         configs = configs,
         refs = specs.toList().map { InstanceSpecRef(it) },
         tagExpression = tagExpression,
         extensions = extensions,
      )
   }

   fun withClasses(vararg specs: KClass<out Spec>): TestEngineLauncher = withClasses(specs.toList())
   fun withClasses(specs: List<KClass<out Spec>>): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configuration = configuration,
         configs = configs,
         refs = specs.toList().map { ReflectiveSpecRef(it) },
         tagExpression = tagExpression,
         extensions = extensions,
      )
   }

   /**
    * Adds a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   @Deprecated("Use withProjectConfig. Will be removed once compiler plugins are updated")
   fun withConfig(vararg projectConfig: AbstractProjectConfig): TestEngineLauncher {
      return withProjectConfig(*projectConfig)
   }

   /**
    * Adds a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   fun withProjectConfig(vararg projectConfig: AbstractProjectConfig): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configuration = configuration,
         configs = configs + projectConfig,
         refs = refs,
         tagExpression = tagExpression,
         extensions = extensions,
      )
   }

   fun withTagExpression(expression: TagExpression?): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configuration = configuration,
         configs = configs,
         refs = refs,
         tagExpression = expression,
         extensions = extensions,
      )
   }

   /**
    * Adds the specified [extensions] to this configuration.
    *
    * Calling [withConfiguration] after invoking this method, will clear these extensions.
    */
   fun withExtensions(vararg extensions: Extension): TestEngineLauncher = withExtensions(extensions.toList())

   /**
    * Adds the specified [extensions] to this configuration.
    *
    * Calling [withConfiguration] after invoking this method, will clear these extensions.
    */
   fun withExtensions(extensions: List<Extension>): TestEngineLauncher {
      extensions.forEach { configuration.registry().add(it) }
      return this
   }

   /**
    * Adds the mutable configuration specified by [configuration] to this launcher,
    * replacing any configuration already present.
    */
   fun withConfiguration(configuration: MutableConfiguration): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configuration = configuration,
         configs = configs,
         refs = refs,
         tagExpression = tagExpression,
         extensions = this.extensions + extensions,
      )
   }

   fun toConfig(): TestEngineConfig {

      // if the engine was configured with explicit tags, we register those via a tag extension
      tagExpression?.let { configuration.registry().add(SpecifiedTagsTagExtension(it)) }

      return TestEngineConfig(
         listener = ThreadSafeTestEngineListener(
            PinnedSpecTestEngineListener(
               listener
            )
         ),
         interceptors = testEngineInterceptors(),
         configuration = ConfigManager.initialize(configuration, configs + detectAbstractProjectConfigs()),
         tags = this.tagExpression ?: TagExpression.Empty,
      )
   }

   fun testSuite(): TestSuite = TestSuite(refs)

   /**
    * Launch the [TestEngine] in an existing coroutine without blocking.
    */
   suspend fun async(): EngineResult {
      log { "TestEngineLauncher: Launching Test Engine" }
      val engine = TestEngine(toConfig())
      return engine.execute(testSuite())
   }

   /**
    * Launch the [TestEngine] created from this builder and block the thread until execution has completed.
    * This method will throw on JS.
    */
   fun launch(): EngineResult {
      log { "TestEngineLauncher: Launching Test Engine" }
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
      log { "TestEngineLauncher: Launching Test Engine in Javascript promise" }
      runPromise {
         val engine = TestEngine(toConfig())
         engine.execute(testSuite())
      }
   }
}
