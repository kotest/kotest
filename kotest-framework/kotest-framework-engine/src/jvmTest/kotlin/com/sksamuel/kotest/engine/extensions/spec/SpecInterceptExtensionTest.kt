package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

@Isolate
class SpecInterceptExtensionTest : FunSpec() {
   init {

      test("SpecInterceptExtension should be invoked IsolationMode.Single") {
         var count = 0

         val ext = object : SpecInterceptExtension {
            override suspend fun interceptSpec(spec: Spec, process: suspend (Spec) -> Unit) {
               process(spec)
               count++
            }
         }

         configuration.register(ext)
         TestEngineLauncher(NoopTestEngineListener).withClasses(SpecInterceptSingleInstance::class).launch()
         configuration.deregister(ext)
         count shouldBe 1
      }

      test("SpecInterceptExtension should be invoked for each instance created") {
         var count = 0

         val ext = object : SpecInterceptExtension {
            override suspend fun interceptSpec(spec: Spec, process: suspend (Spec) -> Unit) {
               process(spec)
               count++
            }
         }

         configuration.register(ext)
         TestEngineLauncher(NoopTestEngineListener).withClasses(SpecInterceptInstancePerTest::class).launch()
         configuration.deregister(ext)
         count shouldBe 3
      }

      test("SpecInterceptExtension can opt to skip processing") {

         val ext = object : SpecInterceptExtension {
            override suspend fun interceptSpec(spec: Spec, process: suspend (Spec) -> Unit) {}
         }

         configuration.register(ext)
         val collecting = CollectingTestEngineListener()
         TestEngineLauncher(collecting).withClasses(SpecInterceptInstancePerTest::class).launch()
         configuration.deregister(ext)
         collecting.tests.shouldBeEmpty()
      }
   }
}

private class SpecInterceptSingleInstance : FunSpec() {
   init {
      isolationMode = IsolationMode.SingleInstance
      test("a") {}
      test("b") {}
   }
}


private class SpecInterceptInstancePerTest : FunSpec() {
   init {
      isolationMode = IsolationMode.InstancePerTest
      test("a") {}
      test("b") {}
   }
}
