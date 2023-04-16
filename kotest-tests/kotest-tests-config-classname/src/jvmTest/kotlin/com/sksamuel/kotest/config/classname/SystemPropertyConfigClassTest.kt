package com.sksamuel.kotest.config.classname

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class SystemPropertyConfigClassTest : FunSpec() {
   init {
      test("system property should be used for config") {
         withSystemProperty(
            KotestEngineProperties.configurationClassName,
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
