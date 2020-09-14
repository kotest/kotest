package io.kotest.framework.jdk9

import io.kotest.core.SourceRef

class SourceRefStackWalker {
   companion object {
      @JvmStatic
      fun sourceRef(): SourceRef {
         val stack = StackWalker.getInstance().walk { s ->
            s.dropWhile { it.className.startsWith("io.kotest") || it.className.startsWith("java.lang") }
               .findFirst()
         }
         return stack.map { SourceRef(it.lineNumber, it.fileName ?: "unknown") }.orElse(SourceRef(-1, "unknown"))
      }
   }
}
