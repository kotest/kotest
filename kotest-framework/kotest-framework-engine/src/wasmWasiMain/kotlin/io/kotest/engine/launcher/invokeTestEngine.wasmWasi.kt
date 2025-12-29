@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   // https://github.com/Kotlin/kotlinx.coroutines/issues/4239
   // workaround for delay exiting the wasmWasiNodeRun task
   withContext(Dispatchers.Default) { }

   val result = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      .withTeamCityListener()
      .execute()

   // Wasm/WASI kotlin std-lib has no process exit call so we can't do anything with the result
}
