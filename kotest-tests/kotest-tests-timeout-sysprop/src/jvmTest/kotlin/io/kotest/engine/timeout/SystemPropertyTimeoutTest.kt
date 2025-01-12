package io.kotest.engine.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@EnabledIf(LinuxCondition::class)
class SystemPropertyTimeoutTest : FunSpec() {
   init {

      test("system properties can be used for test timeouts") {
         withSystemProperty(KotestEngineProperties.TIMEOUT, "500") {
            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withClasses(TimeoutTest::class)
               .launch()
            collector.tests.mapKeys { it.key.name.name }["a"]?.isError shouldBe true
         }
      }

      test("system properties can be used for invocation timeouts") {
         withSystemProperty(KotestEngineProperties.INVOCATION_TIMEOUT, "10") {
            val collector = CollectingTestEngineListener()
            TestEngineLauncher(collector)
               .withClasses(TimeoutTest::class)
               .launch()
            collector.tests.mapKeys { it.key.name.name }["a"]?.isError shouldBe true
         }
      }
   }
}

private class TimeoutTest : FunSpec() {
   init {
      test("a").config(invocations = 1000000) {
         delay(100)
      }
   }
}
