package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.BeforeSpecListenerException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      test("BeforeSpecListener's should be triggered for a spec with tests") {

         configuration.registerExtensions(MyBeforeSpecListener)
         counter.set(0)

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyPopulatedSpec3::class)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)
         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 1

         counter.get() shouldBe 5
      }

      test("BeforeSpecListener's exceptions should be propagated to specExit") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyErrorSpec3::class)
            .launch()
         listener.specs.size shouldBe 1
         listener.specs[MyErrorSpec3::class]!!.shouldBeInstanceOf<BeforeSpecListenerException>()
         listener.tests.size shouldBe 0
      }

      test("BeforeSpecExtension's should NOT be triggered for a spec without tests") {

         configuration.registerExtensions(MyBeforeSpecListener)
         counter.set(0)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyErrorSpec3::class)
            .launch()

         configuration.deregisterExtension(MyBeforeSpecListener)

         counter.get() shouldBe 0
      }
   }
}

private val counter = AtomicInteger(0)

private object MyBeforeSpecListener : BeforeSpecListener {
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


private class MyErrorSpec3 : FunSpec() {
   override fun beforeSpec(spec: Spec) {
      error("boom")
   }

   init {
      test("foo") {}
   }
}
