package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@Description("Tests that the default FQN is picked up - if it wasn't, the invocation count would be the default of 0")
class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("default FQN should be used for config class when no sys property override exists") {
         invocations.set(0)
         TestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withClasses(BarTest::class)
            .launch()
         invocations.get() shouldBe 5
      }
   }
}

private val invocations = AtomicInteger(0)

private class BarTest : FunSpec({
   test("bar") {
      invocations.incrementAndGet()
   }
})
