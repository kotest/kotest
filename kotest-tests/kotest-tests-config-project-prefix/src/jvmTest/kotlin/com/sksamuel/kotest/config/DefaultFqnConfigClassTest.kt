package com.sksamuel.kotest.config

import io.kotest.core.annotation.Description
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val invocations = AtomicInteger(0)

@Description("Tests that the project config is detected, if it wasn't, the invocation count would be the default of 1 and not 5")
class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("default FQN should be used for config class when no sys property override exists") {
         invocations.set(0)
         TestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpecRefs(SpecRef.Reference(BarTest::class))
            .execute()
         invocations.get() shouldBe 5
      }
   }
}

private class BarTest : FunSpec({
   test("bar") {
      invocations.incrementAndGet()
   }
})
