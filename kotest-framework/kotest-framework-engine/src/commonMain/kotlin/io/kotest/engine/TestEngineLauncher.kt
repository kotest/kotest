@file:Suppress("unused")

package io.kotest.engine

import io.kotest.common.runBlocking
import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.SpecRef
import io.kotest.engine.spec.InstanceSpecRef
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.mpp.log
import io.kotest.mpp.sysprop
import kotlin.reflect.KClass

/**
 * A builder class for creating and executing tests via the [TestEngine].
 *
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 */
class TestEngineLauncher(
   private val listener: TestEngineListener,
   private val configs: List<AbstractProjectConfig>,
   private val refs: List<SpecRef>,
   private val explicitTags: Tags?,
   private val testFilters: List<TestFilter>,
   private val specFilters: List<SpecFilter>,
   private val dumpConfig: Boolean,
) {

   constructor() : this(
      NoopTestEngineListener,
      emptyList(),
      emptyList(),
      null,
      emptyList(),
      emptyList(),
      sysprop(KotestEngineProperties.dumpConfig, "false") == "true",
   )

   constructor(listener: TestEngineListener) : this(
      listener,
      emptyList(),
      emptyList(),
      null,
      emptyList(),
      emptyList(),
      sysprop(KotestEngineProperties.dumpConfig, "false") == "true",
   )

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs,
         refs = specs.toList().map { InstanceSpecRef(it) },
         explicitTags = explicitTags,
         testFilters = testFilters,
         specFilters = specFilters,
         dumpConfig = dumpConfig,
      )
   }

   fun withClasses(vararg specs: KClass<out Spec>): TestEngineLauncher = withClasses(specs.toList())
   fun withClasses(specs: List<KClass<out Spec>>): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs,
         refs = specs.toList().map { ReflectiveSpecRef(it) },
         explicitTags = explicitTags,
         testFilters = testFilters,
         specFilters = specFilters,
         dumpConfig = dumpConfig,
      )
   }

   /**
    * Adds a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   fun withConfig(vararg projectConfig: AbstractProjectConfig): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs + projectConfig,
         refs = refs,
         explicitTags = explicitTags,
         testFilters = testFilters,
         specFilters = specFilters,
         dumpConfig = dumpConfig,
      )
   }

   fun withExplicitTags(tags: Tags?): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs,
         refs = refs,
         explicitTags = tags,
         testFilters = testFilters,
         specFilters = specFilters,
         dumpConfig = dumpConfig,
      )
   }

   fun withTestFilters(vararg filters: TestFilter): TestEngineLauncher = withTestFilters(filters.toList())
   fun withTestFilters(filters: List<TestFilter>): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs,
         refs = refs,
         explicitTags = explicitTags,
         testFilters = testFilters + filters,
         specFilters = specFilters,
         dumpConfig = dumpConfig,
      )
   }

   fun withSpecFilters(vararg filters: SpecFilter): TestEngineLauncher = withSpecFilters(filters.toList())
   fun withSpecFilters(filters: List<SpecFilter>): TestEngineLauncher {
      return TestEngineLauncher(
         listener = listener,
         configs = configs,
         refs = refs,
         explicitTags = explicitTags,
         testFilters = testFilters,
         specFilters = specFilters + filters,
         dumpConfig = dumpConfig,
      )
   }

   /**
    * Launch the [TestEngine] created from this builder.
    * This variation of launch requires a coroutine since it is suspending.
    */
   suspend fun launch(): EngineResult {
      log { "TestEngineLauncher: Launching Test Engine in fire and forget mode" }

      val config = TestEngineConfig(
         listener = listener,
         interceptors = testEngineInterceptors(configuration),
         ConfigManager.initialize(configuration, configs),
         testFilters,
         specFilters,
         explicitTags,
      )

      val engine = TestEngine(config)
      return engine.execute(TestSuite(refs))
   }

   /**
    * Launch the [TestEngine] created from this builder, block until the engine has completed,
    * and then return any unhandled errors inside the [EngineResult].
    */
   fun sync(): EngineResult {
      log { "TestEngineLauncher: Launching Test Engine in blocking mode" }

      val config = TestEngineConfig(
         listener = listener,
         interceptors = testEngineInterceptors(configuration),
         ConfigManager.initialize(configuration, configs),
         testFilters,
         specFilters,
         explicitTags,
      )

      return runBlocking {
         val engine = TestEngine(config)
         engine.execute(TestSuite(refs))
      }
   }
}

