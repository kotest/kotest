@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.TestEngineLauncher

actual suspend fun invokeTestEngineLauncher(launcher: TestEngineLauncher) {
   val result = launcher.async()
   // Wasm/WASI kotlin std lib has no process exit call so we can't do anything with the result
}
