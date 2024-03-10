package com.sksamuel.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.longs.shouldBeGreaterThan

// spec test listener callbacks should include construction time
class SpecResultDurationTest : FunSpec() {
   init {

      test("spec finished callback should include construction time in duration") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(Wobble::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.specs.values.first().duration.inWholeMicroseconds.shouldBeGreaterThan(1000)
      }
   }
}

private class Wobble : FunSpec() {
   init {
      Thread.sleep(1000)
      test("foo") {}
   }
}
