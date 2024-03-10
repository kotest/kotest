package com.sksamuel.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class ConstructorExtensionTest : FunSpec() {
   init {

      val c = ProjectConfiguration()
      c.registry.add(ErroringConstructorExtension())
      c.includePrivateClasses = true

      val collector = CollectingTestEngineListener()

      TestEngineLauncher(collector)
         .withClasses(Dummy::class)
         .withConfiguration(c)
         .launch()

      collector.specs[Dummy::class]!!.errorOrNull shouldBe IllegalStateException("THWACK!")
   }
}

private class Dummy : FunSpec() {
   init {
      test("a") {}
   }
}

private class ErroringConstructorExtension : ConstructorExtension {
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      error("THWACK!")
   }
}
