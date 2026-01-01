package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.EngineResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.runPromise
import kotlinx.coroutines.await
import kotlin.js.Promise

@Suppress("UNCHECKED_CAST", "unused")
actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   val launcher = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)

   val promise: Promise<EngineResult> = runPromise {
      launcher.execute()
   } as Promise<EngineResult>

   val result = promise.await()

   if (result.testFailures) {
      error("Tests failed")
   }

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
