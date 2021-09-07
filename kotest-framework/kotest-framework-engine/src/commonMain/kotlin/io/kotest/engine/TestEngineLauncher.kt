@file:Suppress("unused")

package io.kotest.engine

import io.kotest.common.runBlocking
import io.kotest.common.runPromise
import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.detectAbstractProjectConfigs
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
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

   fun toConfig(): TestEngineConfig {

      ConfigManager.initialize(configuration, configs + detectAbstractProjectConfigs())

      return TestEngineConfig(
         listener = ThreadSafeTestEngineListener(
            PinnedSpecTestEngineListener(
               listener
            )
         ),
         interceptors = testEngineInterceptors(configuration),
         configuration,
         testFilters,
         specFilters,
         explicitTags,
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
