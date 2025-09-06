@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult
import io.kotest.engine.extensions.MultipleExceptions

actual fun handleEngineResult(result: EngineResult) {
   if (result.testFailures) {
      error("Tests failed")
   }
   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
