package io.kotest.core

object console {
   fun log(message: Any?) = jsConsoleLog(message as? String ?: "(null)")
}

internal expect fun jsConsoleLog(message: String)
