package com.sksamuel.kotest.config.classname

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

class SystemPropertyConfigClassTest : FunSpec() {
   init {

      test("system property should be used for config") {
         withSystemProperty(
            KotestEngineProperties.configurationClassNames,
            "com.sksamuel.kotest.config.classname.WibbleConfig"
         ) {
            val projectConfiguration = ProjectConfiguration()
            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withConfiguration(projectConfiguration)
               .withClasses(FooTest::class)
               .launch()
            collector.result("a")?.errorOrNull?.message shouldBe "Test 'a' did not complete within 1ms"
         }
      }

      test("support merging") {
         withSystemProperty(
            KotestEngineProperties.configurationClassNames,
            "com.sksamuel.kotest.config.classname.Config1;com.sksamuel.kotest.config.classname.Config2"
         ) {
            val projectConfiguration = ProjectConfiguration()
            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withConfiguration(projectConfiguration)
               .withClasses(MultiConfigTest::class)
               .launch()
            collector.result("a")?.errorOrNull?.message shouldBe "Test 'a' did not complete within 1ms"
         }
      }
   }
}

class WibbleConfig : AbstractProjectConfig() {
   override val invocationTimeout: Long = 1
}

private class FooTest : FunSpec({
   test("a") {
      delay(10000000)
   }
})

private class MultiConfigTest : WordSpec() {
   init {
      "detecting two configs" should {
         "merge listeners" {
            counter.get() shouldBe 2
         }
         "merge project listeners" {
            beforeAll.get() shouldBe 2
         }
         // todo
         "merge separate settings" {
            configuration.testCaseOrder shouldBe TestCaseOrder.Random
            configuration.specExecutionOrder shouldBe SpecExecutionOrder.Random
         }
      }
   }
}

val counter = AtomicInteger(0)
val beforeAll = AtomicInteger(0)

object MyExtension : TestListener {
   override suspend fun beforeEach(testCase: TestCase) {
      counter.incrementAndGet()
   }
}

class Config1 : AbstractProjectConfig() {
   override val testCaseOrder = TestCaseOrder.Random
   override fun extensions(): List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}

class Config2 : AbstractProjectConfig() {
   override val specExecutionOrder: SpecExecutionOrder = SpecExecutionOrder.Random
   override fun extensions(): List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}
