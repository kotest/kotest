@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult

actual fun handleEngineResult(result: EngineResult) {
   if (result.testFailures) {
      error("Tests failed")
   }
}
