package io.kotest.extensions.testcontainers.options

import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer

class BasicLogConsumer(private val type: LogTypes) : Consumer<OutputFrame> {
   override fun accept(t: OutputFrame) {
      when (t.type) {
         OutputFrame.OutputType.STDOUT if (type == LogTypes.STDOUT || type == LogTypes.ALL) -> println(t.utf8String)
         OutputFrame.OutputType.STDERR if (type == LogTypes.STDERR || type == LogTypes.ALL) -> println(t.utf8String)
         OutputFrame.OutputType.END -> println(t.utf8String)
         else -> Unit
      }
   }
}

enum class LogTypes { NONE, STDOUT, STDERR, ALL }
