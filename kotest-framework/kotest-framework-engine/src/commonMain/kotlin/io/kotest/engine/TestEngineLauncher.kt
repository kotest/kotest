@file:Suppress("unused")

package io.kotest.engine

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ConfigManager
import io.kotest.mpp.log

/**
 * Entry point for tests generated through the compiler plugins, and so the
 * public api cannot have breaking changes.
 */
class TestEngineLauncher(
   private val configs: List<AbstractProjectConfig>,
   private val specs: List<Spec>,
) {

   constructor() : this(emptyList(), emptyList())

   fun withSpecs(vararg specs: Spec): TestEngineLauncher {
      return TestEngineLauncher(configs, specs.toList())
   }

   /**
    * Adds a [AbstractProjectConfig] that was detected by the compiler plugin.
    */
   fun withConfig(vararg projectConfig: AbstractProjectConfig): TestEngineLauncher {
      return TestEngineLauncher(configs + projectConfig, specs)
   }

   fun launch() {
      log { "TestEngineLauncher: Creating Test Engine" }

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
