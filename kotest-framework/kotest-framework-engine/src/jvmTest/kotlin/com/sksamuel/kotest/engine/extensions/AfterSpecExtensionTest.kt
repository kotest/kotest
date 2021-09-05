package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.listeners.AfterSpecListener
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

         configuration.registerExtension(MyAfterSpecListener)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec2::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyAfterSpecListener)

         MyAfterSpecListener.invoked.get() shouldBe true
      }

      test("MyAfterSpecListener's should NOT be triggered for a spec without tests") {

         MyAfterSpecListener.invoked.set(false)
         configuration.registerExtension(MyAfterSpecListener)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec2::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyAfterSpecListener)

         MyAfterSpecListener.invoked.get() shouldBe false
      }
   }
}

object MyAfterSpecListener : AfterSpecListener {

   val invoked = AtomicBoolean(false)

   override suspend fun afterSpec(spec: Spec) {
      invoked.set(true)
   }

   override val name: String = "MyAfterSpecListener"
}

private class MyEmptySpec2 : FunSpec()

private class MyPopulatedSpec2 : FunSpec() {
   init {
      test("foo") {}
   }
}
