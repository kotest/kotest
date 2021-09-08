package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.configuration
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class PostInstantiationExtensionTest : FunSpec() {
   init {

      extension(MyPostInstantiationExtension)

      test("post instantiation extensions should be triggered") {

         var a: String? = null

         val listener = object : TestEngineListener {
            override suspend fun specInstantiated(spec: Spec) {
               a = (spec as MySpec).a
            }
         }

         configuration.registerExtension(MyPostInstantiationExtension)

         KotestEngineLauncher()
            .withSpec(MySpec::class)
            .withListener(listener)
            .launch()

         configuration.deregisterExtension(MyPostInstantiationExtension)

         a shouldBe "foo"
      }
   }
}

private val MyPostInstantiationExtension = object : PostInstantiationExtension {
   override fun process(spec: Spec): Spec {
      (spec as MySpec).a = "foo"
      return spec
   }
}

private class MySpec : FunSpec() {
   var a: String? = null
}
