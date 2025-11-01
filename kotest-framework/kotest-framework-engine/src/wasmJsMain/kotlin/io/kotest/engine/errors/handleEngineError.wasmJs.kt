@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult
import io.kotest.engine.js.exitProcess
import io.kotest.engine.js.printStderr
import io.kotest.engine.js.isNodeJsRuntime

actual fun handleEngineResult(result: EngineResult) {
   if (isNodeJsRuntime()) {
      if (result.errors.isNotEmpty()) {
         printStderr(result.errors.first().stackTraceToString())
         exitProcess(1)
      } else if (result.testFailures) {
         exitProcess(1)
      }
   } else {
      // throwing here shows "Disconnected (0 times) , because no message in 30000 ms."
      // see KT-73911 https://youtrack.jetbrains.com/issue/KT-73911
      // so nothing we can do at the moment
   }
}
