package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.ApplyExtension
import io.kotest.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@EnabledIf(LinuxCondition::class)
class ConstructorExtensionTest : FunSpec() {
   init {

      test("constructor extension should be applied") {
         val c = ProjectConfiguration()
         c.registry.add(ErroringConstructorExtension())

         val collector = CollectingTestEngineListener()

         TestEngineLauncher(collector)
            .withClasses(DummySpec::class)
            .withConfiguration(c)
            .launch()

         // the extension was applied then the instantiation will fail
         collector.specs[DummySpec::class]!!.errorOrNull shouldBe IllegalStateException("THWACK!")
      }

      test("constructor extension should be applied from ApplyExtension") {

         val collector = CollectingTestEngineListener()

         TestEngineLauncher(collector)
            .withClasses(FunkySpec::class)
            .launch()

         // if the extension isn't applied, it would fail to instantiate the class
         collector.specs[FunkySpec::class]!!.isSuccess shouldBe true
      }
   }
}

private class DummySpec : FunSpec() {
   init {
      test("a") {}
   }
}

private class ErroringConstructorExtension : ConstructorExtension {
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      error("THWACK!")
   }
}

class SpecialConstructorExtension : ConstructorExtension {
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec {
      return FunkySpec("hello")
   }
}

@ApplyExtension(SpecialConstructorExtension::class)
private class FunkySpec(private val a: String) : FunSpec() {
   init {
      test("funky") {}
   }
}
