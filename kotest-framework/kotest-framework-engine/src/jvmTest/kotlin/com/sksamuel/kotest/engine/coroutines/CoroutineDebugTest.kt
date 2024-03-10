package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CoroutineDebugTest : FunSpec() {
   init {
      test("coroutine debug should dump coroutine stacks on error") {

         val c = ProjectConfiguration()
         c.coroutineDebugProbes = true
         c.includePrivateClasses = true

         val output = captureStandardOut {
            TestEngineLauncher(NoopTestEngineListener)
               .withClasses(Wibble::class)
               .withConfiguration(c)
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
