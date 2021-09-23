package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

@Isolate
class MySpecInitializerExtensionTest : FunSpec() {
   init {

      test("SpecFinalizerExtension's should be triggered for a spec with tests") {

         configuration.registerExtension(MySpecInitializeExtension)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec4::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MySpecInitializeExtension)

         MySpecInitializeExtension.invoked.get() shouldBe true
      }

      test("SpecFinalizerExtension's should be triggered for a spec without tests") {

         MySpecFinalizeExtension.finalized.set(false)
         configuration.registerExtension(MySpecInitializeExtension)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec4::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MySpecInitializeExtension)

         MySpecInitializeExtension.invoked.get() shouldBe true
      }
   }
}

object MySpecInitializeExtension : SpecInitializeExtension {
   val invoked = AtomicBoolean(false)
   override suspend fun initialize(spec: Spec) {
      invoked.set(true)
   }
}

private class MyEmptySpec4 : FunSpec()

private class MyPopulatedSpec4 : FunSpec() {
   init {
      test("foo") {}
   }
}
