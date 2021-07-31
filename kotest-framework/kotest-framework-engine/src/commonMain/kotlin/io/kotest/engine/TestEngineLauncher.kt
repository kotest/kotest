@file:Suppress("unused")

package io.kotest.engine

import io.kotest.core.spec.Spec

/**
 * Entry point for tests generated through the complier plugins, and so the
 * public api cannot have breaking changes.
 */
class TestEngineLauncher {

   private val specs = mutableListOf<Spec>()

   fun register(vararg specs: Spec): TestEngineLauncher {
      specs.forEach { this.specs.add(it) }
      return this
   }

   fun launch() {
      val config = TestEngineConfig.default()
      val engine = TestEngine(config)
      engine.execute(TestSuite(specs, emptyList()))
   }
}
