package io.kotest.engine.listener

import io.kotest.engine.console.consoleRenderer

/**
 * An extension of the [ConsoleTestEngineListener] that adds JVM support for stacktraces.
 */
class EnhancedConsoleTestEngineListener : ConsoleTestEngineListener() {

   override fun printlnStackTrace(error: Throwable, padding: Int) {
      error.stackTrace?.forEach {
         consoleRenderer.println(consoleRenderer.red("".padStart(padding + 2, ' ') + it))
      }
   }
}
