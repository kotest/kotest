package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class SystemPropertyConfigClassTest : FunSpec() {
   init {

      test("system property should be used for config") {
         withSystemProperty(
            KotestEngineProperties.configurationClassNames,
            "com.sksamuel.kotest.config.classname.WibbleConfig"
         ) {
            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withClasses(FooTest::class)
               .launch()
            collector.result("a")?.errorOrNull?.message shouldBe "Test 'a' did not complete within 1ms"
         }
      }
   }
}

class WibbleConfig : AbstractProjectConfig() {
   override val invocationTimeout = 1.milliseconds
}

private class FooTest : FunSpec({
   test("a") {
      delay(10000000)
   }
})

private class MultiConfigTest : FunSpec() {
   init {
      test("a") {
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
   override val extensions: List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}

class Config2 : AbstractProjectConfig() {
   override val extensions: List<Extension> = listOf(MyExtension)
   override suspend fun beforeProject() {
      beforeAll.incrementAndGet()
   }
}
