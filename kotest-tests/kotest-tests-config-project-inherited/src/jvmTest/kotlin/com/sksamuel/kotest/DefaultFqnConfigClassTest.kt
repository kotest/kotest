package com.sksamuel.kotest

import io.kotest.core.annotation.Description
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.provided.ProjectConfig
import java.util.concurrent.atomic.AtomicInteger

@Description("Tests that the inherited project config is used, otherwise the invocation count would be 1")
class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("inherited project config") {
         invocations.set(0)
         TestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpecRefs(SpecRef.Reference(BarTest::class))
            .execute()
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

class InheritedProjectConfig : ProjectConfig() {}
