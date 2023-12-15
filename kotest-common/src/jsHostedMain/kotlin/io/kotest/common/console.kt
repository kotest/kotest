package io.kotest.common

object console {
   fun log(message: Any?) = jsConsoleLog(message as? String ?: "(null)")
}

expect internal fun jsConsoleLog(message: String)
