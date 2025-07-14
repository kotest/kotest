package com.sksamuel.kotest.engine.extensions.spec.finalizespec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
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

@EnabledIf(LinuxOnlyGithubCondition::class)
class FinalizeSpecTest : FunSpec() {
   init {
      test("finalize spec listeners should be fired") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(FinalizeSpecTestListener1(), FinalizeSpecTestListener2())
         }
         counter.set(0)
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(FinalizeSpec::class)
            .withProjectConfig(c)
            .launch()

         counter.get().shouldBe(2)
      }
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class FinalizeSpec : FunSpec() {
   init {
      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
   }
}
