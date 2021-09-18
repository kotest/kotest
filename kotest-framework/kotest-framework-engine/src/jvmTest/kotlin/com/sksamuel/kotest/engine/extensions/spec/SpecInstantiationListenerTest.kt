package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@Isolate
class SpecInstantiationListenerTest : FunSpec() {
   init {
      test("SpecInstantiationListener.specInstantiated should be notified on success") {

         var fired = false

         val ext = object : SpecInstantiationListener {
            override fun specInstantiated(spec: Spec) {
               fired = true
            }

            override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
               error("boom")
            }
         }

         configuration.registerExtensions(ext)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(SpecInstantiationSuccessSpec::class)
            .launch()

         configuration.deregisterExtension(ext)
         fired shouldBe true
      }

      test("SpecInstantiationListener.specInstantiationError should be notified on failure") {

         var fired = false

         val ext = object : SpecInstantiationListener {
            override fun specInstantiated(spec: Spec) {
               error("boom")
            }

            override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
               fired = true
            }
         }
         configuration.registerExtensions(ext)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(SpecInstantiationFailureSpec::class)
            .launch()

         configuration.deregisterExtension(ext)
         fired shouldBe true
      }
   }
}

private class SpecInstantiationSuccessSpec : FunSpec() {
   init {
      test("a") {}
   }
}

private class SpecInstantiationFailureSpec : FunSpec() {
   init {
      error("zapp!")
   }
}
