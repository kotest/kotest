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
class AfterSpecExtensionTest : FunSpec() {
   init {

      test("AfterSpecExtension's should be triggered for a spec with tests") {

         configuration.registerExtension(MyAfterSpecExtension)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec2::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyAfterSpecExtension)

         MyAfterSpecExtension.invoked.get() shouldBe true
      }

      test("AfterSpecExtension's should NOT be triggered for a spec without tests") {

         MyAfterSpecExtension.invoked.set(false)
         configuration.registerExtension(MyAfterSpecExtension)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec2::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyAfterSpecExtension)

         MyAfterSpecExtension.invoked.get() shouldBe false
      }
   }
}

object MyAfterSpecExtension : AfterSpecExtension {
   val invoked = AtomicBoolean(false)
   override fun afterSpec(spec: Spec) {
      invoked.set(true)
   }
}

private class MyEmptySpec2 : FunSpec()

private class MyPopulatedSpec2 : FunSpec() {
   init {
      test("foo") {}
   }
}
