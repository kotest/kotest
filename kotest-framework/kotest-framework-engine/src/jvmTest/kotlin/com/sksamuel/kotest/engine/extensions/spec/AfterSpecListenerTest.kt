package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.AfterSpecListenerException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class AfterSpecListenerTest : FunSpec() {
   init {

      test("AfterSpecListener's should be triggered for a spec with tests") {

         configuration.registerExtensions(MyAfterSpecListener)
         counter.set(0)

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyPopulatedSpec2::class)
            .launch()

         configuration.deregisterExtension(MyAfterSpecListener)
         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 1

         counter.get() shouldBe 5
      }

      test("AfterSpecListener's exceptions should be propagated to specExit") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(MyErrorSpec2::class)
            .launch()
         listener.specs.size shouldBe 1
         listener.specs[MyErrorSpec2::class]!!.shouldBeInstanceOf<AfterSpecListenerException>()
         listener.tests.size shouldBe 1
      }

      test("AfterSpecListener's should NOT be triggered for a spec without tests") {

         configuration.registerExtensions(MyAfterSpecListener)
         counter.set(0)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyEmptySpec2::class)
            .launch()

         configuration.deregisterExtension(MyAfterSpecListener)

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

private class MyErrorSpec2 : FunSpec() {
   override fun extensions(): List<Extension> {
      return listOf(object : AfterSpecListener {
         override suspend fun afterSpec(spec: Spec) {
            error("zapp!")
         }
      })
   }

   init {
      test("foo") {}
   }
}

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
