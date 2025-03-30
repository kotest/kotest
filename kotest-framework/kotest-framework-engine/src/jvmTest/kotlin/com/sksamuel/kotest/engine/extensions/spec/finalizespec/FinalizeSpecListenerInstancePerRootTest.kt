package com.sksamuel.kotest.engine.extensions.spec.finalizespec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

private val counter = AtomicInteger(0)

class FinalizeSpecListenerPerTest1 : TestListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpecListenerInstancePerRootTest::class) {
         counter.incrementAndGet()
      }
   }
}

class FinalizeSpecListenerPerTest2 : FinalizeSpecListener {
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (kclass == FinalizeSpecListenerInstancePerRootTest::class) {
         counter.incrementAndGet()
      }
   }
}

@ApplyExtension(FinalizeSpecListenerPerTest1::class, FinalizeSpecListenerPerTest2::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class FinalizeSpecListenerInstancePerRootTest : FunSpec() {

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   init {

      afterProject {
         // both listeners should have fired
         counter.get() shouldBe 2
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      context("d") {
         test("e").config(enabled = true) {}
      }
   }
}
