package com.sksamuel.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@EnabledIf(LinuxOnlyGithubCondition::class)
class ConstructorExtensionTest : FunSpec() {
   init {

      test("constructor extension should be applied") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ErroringConstructorExtension())
         }

         val collector = CollectingTestEngineListener()

         TestEngineLauncher(collector)
            .withClasses(DummySpec::class)
            .withProjectConfig(c)
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

@Suppress("unused")
@ApplyExtension(SpecialConstructorExtension::class)
private class FunkySpec(private val a: String) : FunSpec() {
   init {
      test("funky") {}
   }
}
