package io.kotest.core

actual fun jsConsoleLog(message: String) {
   js("console.log(message)")
}
