package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class AfterSpecListenerTest : FunSpec() {
   init {

      test("AfterSpecListener's should be triggered for a spec with tests") {

         counter.set(0)

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyAfterSpecListener)
         }

         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(MyPopulatedSpec2::class)
            .withProjectConfig(c)
            .launch()

         collector.specs.size shouldBe 1
         collector.tests.size shouldBe 1

         counter.get() shouldBe 5
      }

      test("AfterSpecListener's exceptions should be propagated to specExit") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(MyErrorSpec2::class)
            .launch()
         collector.specs.size shouldBe 1
         collector.specs[MyErrorSpec2::class]!!.errorOrNull.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
         collector.tests.size shouldBe 1
      }

      test("AfterSpecListener's should NOT be triggered for a spec without defined tests") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyAfterSpecListener)
         }
         counter.set(0)

         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(MyEmptySpec2::class)
            .withProjectConfig(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("AfterSpecListener's should NOT be triggered for a spec without active tests") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyAfterSpecListener)
         }
         counter.set(0)

         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(NoActiveTestsSpec2::class)
            .withProjectConfig(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("inline afterSpec functions should be invoked") {
         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(InlineAfterSpec::class)
            .launch()
         inlineAfterSpec.shouldBeTrue()
      }

      context("handle inline afterSpec exceptions") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
         ) { isolationMode ->
            val collector = CollectingTestEngineListener()
            val config = object : AbstractProjectConfig() {
               override val isolationMode = isolationMode
            }
            TestEngineLauncher().withListener(collector)
               .withProjectConfig(config)
               .withClasses(InlineAfterSpecError::class)
               .launch()
            collector.specs.size.shouldBe(1)
            collector.specs[InlineAfterSpecError::class]!!.errorOrNull.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
         }
      }
   }
}

private val counter = AtomicInteger(0)

private object MyAfterSpecListener : AfterSpecListener {
   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }
}

private class MyEmptySpec2 : FunSpec()

private class MyErrorSpec2 : FunSpec() {
   override val extensions: List<Extension> = listOf(object : AfterSpecListener {
      override suspend fun afterSpec(spec: Spec) {
         error("zapp!")
      }
   })

   init {
      test("foo") {}
   }
}

private class NoActiveTestsSpec2 : FunSpec() {
   override val extensions: List<Extension> = listOf(object : AfterSpecListener {
      override suspend fun afterSpec(spec: Spec) {
         error("zapp!")
      }
   })

   init {
      xtest("foo1") {}
      test("!foo2") {}
      test("foo3").config(enabled = false) {}
   }
}

private class MyPopulatedSpec2 : FunSpec() {

   override suspend fun afterSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   override val extensions: List<Extension> = listOf(
      MyAfterSpecListener,
      object : TestListener {
         override suspend fun afterSpec(spec: Spec) {
            counter.incrementAndGet()
         }
      }
   )

   init {

      afterSpec { counter.incrementAndGet() }

      test("foo") {}
   }
}

var inlineAfterSpec = false

private class InlineAfterSpec : FunSpec() {
   init {
      afterSpec { inlineAfterSpec = true }
      test("a") {}
   }
}

private class InlineAfterSpecError : FunSpec() {
   init {
      afterSpec { error("zam!") }
      test("a") {}
   }
}
