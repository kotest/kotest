package io.kotest.extensions.testcontainers.options

import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer

class StandardLogConsumer(private val type: LogTypes = LogTypes.ALL) : Consumer<OutputFrame> {
   override fun accept(t: OutputFrame) {
      when (t.type) {
         OutputFrame.OutputType.STDOUT if (type == LogTypes.STDOUT || type == LogTypes.ALL) -> print(t.utf8String)
         OutputFrame.OutputType.STDERR if (type == LogTypes.STDERR || type == LogTypes.ALL) -> print(t.utf8String)
         OutputFrame.OutputType.END -> print(t.utf8String)
         else -> Unit
      }
   }
}

enum class LogTypes { NONE, STDOUT, STDERR, ALL }
