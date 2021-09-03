@file:Suppress("unused")

package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.configuration
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.mpp.log

/**
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 */
class TestEngineLauncher(
   private val configs: List<AbstractProjectConfig>,
   private val specs: List<Spec>,
   private val explicitTags: Tags?,
   private val testFilters: List<TestFilter>,
) {

   constructor() : this(emptyList(), emptyList(), null, emptyList())

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(configs, specs.toList(), explicitTags, testFilters)
   }

   /**
    * Adds a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   fun withConfig(vararg projectConfig: AbstractProjectConfig): TestEngineLauncher {
      return TestEngineLauncher(configs + projectConfig, specs, explicitTags, testFilters)
   }

   fun withExplicitTags(explicitTags: Tags): TestEngineLauncher {
      return TestEngineLauncher(configs, specs, explicitTags, testFilters)
   }

   fun withTestFilters(filters: List<TestFilter>): TestEngineLauncher {
      return TestEngineLauncher(configs, specs, explicitTags, filters)
   }

   fun launch() {
      log { "TestEngineLauncher: Creating Test Engine" }

      // if the engine was invoked with explicit tags, we register those via a tag extension
      explicitTags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(testFilters)

      val config = TestEngineConfig.default()
         // initializes the global configuration and passes it to the test engine config
         .withConfig(ConfigManager.initialize(configuration, configs))

      val engine = TestEngine(config)
      runSuspend {
         engine.execute(TestSuite(specs, emptyList()))
      }
   }
}

expect fun runSuspend(f: suspend () -> Unit)
