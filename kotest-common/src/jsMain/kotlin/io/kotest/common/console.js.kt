package io.kotest.common

actual fun jsConsoleLog(message: String) {
   js("console.log(message)")
}
