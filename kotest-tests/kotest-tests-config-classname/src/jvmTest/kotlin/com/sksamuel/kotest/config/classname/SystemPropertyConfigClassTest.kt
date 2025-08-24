package com.sksamuel.kotest.config.classname

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class SystemPropertyConfigClassTest : FunSpec() {
   init {
      test("system property override should be used for config classname") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(FooTest::class)
            .launch()
         collector.result("a")?.errorOrNull?.message shouldBe "Test 'a' did not complete within 15ms"
      }
   }
}

class WibbleConfig : AbstractProjectConfig() {
   override val invocationTimeout = 15.milliseconds
}

private class FooTest : FunSpec({
   test("a") {
      delay(10000000)
   }
})
