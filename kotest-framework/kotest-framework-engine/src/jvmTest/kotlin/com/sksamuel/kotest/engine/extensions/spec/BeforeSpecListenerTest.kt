package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      test("BeforeSpecListener's should be triggered for a spec with tests") {

         configuration.registerExtension(MyBeforeSpecListener)

         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec3::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)

         MyBeforeSpecListener.invoked.get() shouldBe true
      }

      test("BeforeSpecExtension's should NOT be triggered for a spec without tests") {

         MyBeforeSpecListener.invoked.set(false)
         configuration.registerExtension(MyBeforeSpecListener)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec3::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)

         MyBeforeSpecListener.invoked.get() shouldBe false
      }
   }
}

object MyBeforeSpecListener : BeforeSpecListener {
   val invoked = AtomicBoolean(false)
   override val name: String = "MyBeforeSpecExtension"
   override suspend fun beforeSpec(spec: Spec) {
      invoked.set(true)
   }
}

private class MyEmptySpec3 : FunSpec()

private class MyPopulatedSpec3 : FunSpec() {
   init {
      test("foo") {}
   }
}
