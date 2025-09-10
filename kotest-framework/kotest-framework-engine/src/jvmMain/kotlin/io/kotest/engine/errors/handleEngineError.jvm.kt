@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult
import io.kotest.engine.extensions.MultipleExceptions
import kotlin.system.exitProcess

actual fun handleEngineResult(result: EngineResult): Unit {
   if (result.testFailures) {
      // the kotest task test will pick up return code as 1 as failed errors
      exitProcess(1)
   }
   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
