package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecFinalizeExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

@Isolate
class SpecFinalizerExtensionTest : FunSpec() {
   init {

      test("SpecFinalizerExtension's should be triggered for a spec with tests") {

         configuration.registerExtension(MySpecFinalizeExtension)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MySpecFinalizeExtension)

         MySpecFinalizeExtension.finalized.get() shouldBe true
      }

      test("SpecFinalizerExtension's should be triggered for a spec without tests") {

         MySpecFinalizeExtension.finalized.set(false)
         configuration.registerExtension(MySpecFinalizeExtension)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MySpecFinalizeExtension)

         MySpecFinalizeExtension.finalized.get() shouldBe true
      }
   }
}

object MySpecFinalizeExtension : SpecFinalizeExtension {
   val finalized = AtomicBoolean(false)
   override fun finalize(spec: Spec) {
      finalized.set(true)
   }
}

private class MyEmptySpec : FunSpec()

private class MyPopulatedSpec : FunSpec() {
   init {
      test("foo") {}
   }
}
