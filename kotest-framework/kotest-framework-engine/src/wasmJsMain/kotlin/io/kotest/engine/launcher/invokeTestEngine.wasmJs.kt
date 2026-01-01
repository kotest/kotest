@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.EngineResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.js.exitProcess
import io.kotest.engine.js.isNodeJsRuntime
import io.kotest.engine.js.printStderr
import kotlinx.coroutines.await
import kotlin.js.Promise

@Suppress("OPT_IN_USAGE")
actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {
   val promise = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      .withTeamCityListener()
      .promise() as Promise<JsAny?>
   val result = promise.await<EngineResult>()
   handleEngineResult(result)
}

private fun handleEngineResult(result: EngineResult) {
   if (isNodeJsRuntime()) {
      if (result.errors.isNotEmpty()) {
         printStderr(result.errors.first().stackTraceToString())
         exitProcess(1)
      } else if (result.testFailures) {
         exitProcess(1)
      }
   } else {
      // throwing here shows "Disconnected (0 times) , because no message in 30000 ms."
      // TODO see KT-73911 https://youtrack.jetbrains.com/issue/KT-73911
      // so nothing we can do at the moment
   }
}
