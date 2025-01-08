package com.sksamuel.kotest.engine.extensions.spec.finalizespec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

private val counter = AtomicInteger(0)

private class FinalizeSpecTestListener1 : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpec::class) {
         counter.incrementAndGet()
      }
   }
}

private class FinalizeSpecTestListener2 : FinalizeSpecListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpec::class) {
         counter.incrementAndGet()
      }
   }
}

@EnabledIf(LinuxCondition::class)
class FinalizeSpecTest : FunSpec() {
   init {
      test("finalize spec listeners should be fired") {

         val c = ProjectConfiguration()
         c.registry.add(FinalizeSpecTestListener1())
         c.registry.add(FinalizeSpecTestListener2())

         counter.set(0)
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(FinalizeSpec::class)
            .withProjectConfig(c)
            .launch()

         counter.get().shouldBe(2)
      }
   }
}

@EnabledIf(LinuxCondition::class)
class FinalizeSpec : FunSpec() {
   init {
      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
   }
}
