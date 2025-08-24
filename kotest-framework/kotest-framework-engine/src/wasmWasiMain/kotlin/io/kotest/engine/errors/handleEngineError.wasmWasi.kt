@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult

actual fun handleEngineResult(result: EngineResult) {
   // Wasm/WASI kotlin std lib has no process exit call
}
