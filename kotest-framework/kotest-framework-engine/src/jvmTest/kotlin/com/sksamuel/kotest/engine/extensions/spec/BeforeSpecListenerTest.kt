package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      test("BeforeSpecListener's should be triggered for a spec with tests") {

         configuration.registerExtension(MyBeforeSpecListener)
         counter.set(0)

         val listener = CollectingTestEngineListener()
         KotestEngineLauncher()
            .withSpec(MyPopulatedSpec3::class)
            .withListener(listener)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)
         listener.tests.size shouldBe 1

         counter.get() shouldBe 5
      }

      test("!BeforeSpecExtension's should NOT be triggered for a spec without tests") {

         configuration.registerExtension(MyBeforeSpecListener)
         counter.set(0)

         KotestEngineLauncher()
            .withSpec(MyEmptySpec3::class)
            .withListener(NoopTestEngineListener)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)

         counter.get() shouldBe 0
      }
   }
}

private val counter = AtomicInteger(0)

object MyBeforeSpecListener : BeforeSpecListener {
   override val name: String = "MyBeforeSpecExtension"
   override suspend fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }
}

private class MyEmptySpec3 : FunSpec()

private class MyPopulatedSpec3 : FunSpec() {

   override fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   override fun extensions(): List<Extension> {
      return listOf(MyBeforeSpecListener)
   }

   override fun listeners(): List<TestListener> {
      return listOf(object : TestListener {
         override suspend fun beforeSpec(spec: Spec) {
            counter.incrementAndGet()
         }
      })
   }

   init {

      beforeSpec { counter.incrementAndGet() }

      test("foo") {}
   }
}
