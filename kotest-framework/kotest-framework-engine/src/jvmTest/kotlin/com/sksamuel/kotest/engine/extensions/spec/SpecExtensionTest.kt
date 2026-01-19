package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.Isolate
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

@Isolate
class SpecExtensionTest : FunSpec() {
   init {

      test("SpecExtension should be invoked when using IsolationMode.SingleInstance") {
         var count = 0

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
               execute(spec)
               count++
            }
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withSpecRefs(SpecRef.Reference(SpecInterceptSingleInstance::class))
            .withProjectConfig(c)
            .execute()

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

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withSpecRefs(SpecRef.Reference(SpecInterceptInstancePerRoot::class))
            .withProjectConfig(c)
            .execute()

         count shouldBe 2
      }

      test("SpecExtension can opt to skip processing") {

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {}
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }

         val collecting = CollectingTestEngineListener()

         TestEngineLauncher().withListener(collecting)
            .withSpecRefs(SpecRef.Reference(SpecInterceptInstancePerRoot::class))
            .withProjectConfig(c)
            .execute()

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


private class SpecInterceptInstancePerRoot : FunSpec() {
   init {
      isolationMode = IsolationMode.InstancePerRoot
      test("a") {}
      test("b") {}
   }
}




