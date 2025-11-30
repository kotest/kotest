package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.Description
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

@Description("We want to test that the project config is picked up, so we'll check that the invocations count was changed from the default")
class SystemPropertyConfigClassTest : FunSpec() {
   init {

      test("system property override should be used for config classname") {
         val collector = CollectingTestEngineListener()
         counter.set(0)
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(FooTest::class)
            .launch()
         counter.get() shouldBe 5
      }
   }
}

class WibbleConfig : AbstractProjectConfig() {
   override val invocations = 5
}

private val counter = AtomicInteger(0)

private class FooTest : FunSpec({
   test("a") {
      counter.incrementAndGet()
   }
})
