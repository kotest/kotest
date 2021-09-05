package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

@Isolate
class BeforeSpecExtensionTest : FunSpec() {
   init {

      test("BeforeSpecExtension's should be triggered for a spec with tests") {

         configuration.registerExtension(MyBeforeSpecExtension)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec3::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyAfterSpecExtension)

         MyBeforeSpecExtension.invoked.get() shouldBe true
      }

      test("BeforeSpecExtension's should NOT be triggered for a spec without tests") {

         MyBeforeSpecExtension.invoked.set(false)
         configuration.registerExtension(MyBeforeSpecExtension)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec3::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecExtension)

         MyBeforeSpecExtension.invoked.get() shouldBe false
      }
   }
}

object MyBeforeSpecExtension : AfterSpecExtension {
   val invoked = AtomicBoolean(false)
   override fun afterSpec(spec: Spec) {
      invoked.set(true)
   }
}

private class MyEmptySpec3 : FunSpec()

private class MyPopulatedSpec3 : FunSpec() {
   init {
      test("foo") {}
   }
}
