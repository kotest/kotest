package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecExtension
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

      test("SpecExtension should be invoked IsolationMode.Single") {
         var count = 0

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
               execute(spec)
               count++
            }
         }

         val conf = io.kotest.core.config.Configuration()
         conf.registry().add(ext)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(SpecInterceptSingleInstance::class)
            .withConfiguration(conf)
            .launch()

         count shouldBe 1
      }

      test("SpecExtension should be invoked for each instance created") {
         var count = 0

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
               execute(spec)
               count++
            }
         }

         configuration.register(ext)
         TestEngineLauncher(NoopTestEngineListener).withClasses(SpecInterceptInstancePerTest::class).launch()
         configuration.deregister(ext)
         count shouldBe 3
      }

      test("SpecExtension can opt to skip processing") {

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {}
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
