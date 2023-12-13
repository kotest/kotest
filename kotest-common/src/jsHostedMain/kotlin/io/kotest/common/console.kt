package io.kotest.common

object console {
   fun log(message: Any?) = jsConsoleLog(message as? String ?: "(null)")
}

private fun jsConsoleLog(message: String): Unit = js("console.log(message)")
// For Wasm compatibility, js() invocations must be a single expression within a top-level function.
