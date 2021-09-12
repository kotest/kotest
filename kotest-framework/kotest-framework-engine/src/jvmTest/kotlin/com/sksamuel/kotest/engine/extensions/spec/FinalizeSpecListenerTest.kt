package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
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

class FinalizeSpecTestListener1 : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpec::class) {
         counter.incrementAndGet()
      }
   }
}

class FinalizeSpecTestListener2 : FinalizeSpecListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpec::class) {
         counter.incrementAndGet()
      }
   }

   override val name: String = "FinalizeSpecTestListener2"
}

class FinalizeSpecTest : FunSpec() {
   init {
      test("finalize spec listeners should be fired") {
         configuration.registerExtension(FinalizeSpecTestListener1())
         configuration.registerExtension(FinalizeSpecTestListener2())
         counter.set(0)
         TestEngineLauncher(NoopTestEngineListener).withClasses(FinalizeSpec::class).launch()
         counter.get().shouldBe(2)
      }
   }
}

class FinalizeSpec : FunSpec() {
   init {
      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
   }
}
