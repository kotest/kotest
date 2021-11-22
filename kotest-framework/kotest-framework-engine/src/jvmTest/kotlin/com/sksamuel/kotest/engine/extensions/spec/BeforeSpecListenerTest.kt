package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      test("BeforeSpecListener's should be triggered for a spec with tests") {

         val c = Configuration()
         c.registry.add(MyBeforeSpecListener)

         counter.set(0)

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyPopulatedSpec3::class)
            .withConfiguration(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 1

         counter.get() shouldBe 5
      }

      test("BeforeSpecExtension's should be triggered for a spec without tests") {

         val c = Configuration()
         c.registry.add(MyBeforeSpecListener)

         counter.set(0)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecErrorNoTests::class)
            .withConfiguration(c)
            .launch()

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener's exceptions should be propagated to specExit") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyErrorSpec3::class)
            .launch()
         listener.specs.size shouldBe 1
         listener.specs[MyErrorSpec3::class]!!.shouldBeInstanceOf<ExtensionException.BeforeSpecException>()
         listener.tests.size shouldBe 0
      }


   }
}

private val counter = AtomicInteger(0)

private object MyBeforeSpecListener : BeforeSpecListener {
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

private class BeforeSpecErrorNoTests : FunSpec() {
   override fun beforeSpec(spec: Spec) {
      error("boom")
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
