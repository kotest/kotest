package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.PostInstantiationExtension
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class PostInstantiationExtensionTest : FunSpec() {
   init {

      extension(MyPostInstantiationExtension)

      test("post instantiation extensions should be triggered") {

         val conf = ProjectConfiguration()
         conf.registry.add(MyPostInstantiationExtension)

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MySpec::class)
            .withConfiguration(conf)
            .launch()

         a shouldBe "foo"
      }
   }
}

private var a: String? = null

private val MyPostInstantiationExtension = object : PostInstantiationExtension {
   override suspend fun instantiated(spec: Spec): Spec {
      a = "foo"
      return spec
   }
}

private class MySpec : FunSpec()
