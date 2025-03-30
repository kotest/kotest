package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

@EnabledIf(NotMacOnGithubCondition::class)
class CoroutineDebugTest : FunSpec() {
   init {
      test("coroutine debug should dump coroutine stacks on error") {

         val p = object : AbstractProjectConfig() {
            override val coroutineDebugProbes = true
         }

         val output = captureStandardOut {
            TestEngineLauncher(NoopTestEngineListener)
               .withClasses(Wibble::class)
               .withProjectConfig(p)
               .launch()
               .errors.shouldBeEmpty()
         }
         output shouldContain "DeferredCoroutine"
      }
   }
}

private class Wibble : FunSpec() {
   init {
      coroutineDebugProbes = true
      test("a") {
         async { delay(1000) }
         error("qwe")
      }
   }
}
