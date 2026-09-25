package io.kotest.engine.listener

import io.kotest.engine.console.consoleRenderer

actual fun printlnStackTrace(error: Throwable, padding: Int) {
   error.stackTrace?.forEach {
      consoleRenderer.println(consoleRenderer.red("".padStart(padding + 2, ' ') + it))
   }
}
