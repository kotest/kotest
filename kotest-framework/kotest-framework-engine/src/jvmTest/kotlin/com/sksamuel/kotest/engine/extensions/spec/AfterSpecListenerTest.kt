package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class AfterSpecListenerTest : FunSpec() {
   init {

      test("AfterSpecListener's should be triggered for a spec with tests") {

         configuration.register(MyAfterSpecListener)
         counter.set(0)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyPopulatedSpec2::class)
            .launch()

         configuration.deregister(MyAfterSpecListener)

         counter.get() shouldBe 5
      }

      test("AfterSpecListener's should NOT be triggered for a spec without tests") {

         configuration.register(MyAfterSpecListener)
         counter.set(0)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyEmptySpec2::class)
            .launch()

         configuration.deregister(MyAfterSpecListener)

         counter.get() shouldBe 0
      }
   }
}

private val counter = AtomicInteger(0)

private object MyAfterSpecListener : AfterSpecListener {
   override val name: String = "MyAfterSpecListener"
   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }
}

private class MyEmptySpec2 : FunSpec()

private class MyPopulatedSpec2 : FunSpec() {

   override fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   override fun extensions(): List<Extension> {
      return listOf(MyAfterSpecListener)
   }

   override fun listeners(): List<TestListener> {
      return listOf(object : TestListener {
         override suspend fun afterSpec(spec: Spec) {
            counter.incrementAndGet()
         }
      })
   }

   init {

      afterSpec { counter.incrementAndGet() }

      test("foo") {}
   }
}
