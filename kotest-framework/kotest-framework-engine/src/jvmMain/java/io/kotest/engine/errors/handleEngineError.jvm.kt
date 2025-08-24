@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult
import kotlin.system.exitProcess

actual fun handleEngineResult(result: EngineResult) {
   exitProcess(1)
}
