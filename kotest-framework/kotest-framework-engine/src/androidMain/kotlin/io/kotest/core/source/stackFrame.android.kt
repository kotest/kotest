package io.kotest.core.source

internal actual fun findFirstStackFrame(
   excludeDataTest: Boolean,
   predicate: (JvmStackFrame) -> Boolean,
): JvmStackFrame? {
   return SourceRefUtils.filteredUserFrames(Thread.currentThread().stackTrace, excludeDataTest).firstNotNullOfOrNull {
      val frame = try {
         JvmStackFrame(Class.forName(it.className), it.lineNumber)
      } catch (_: ReflectiveOperationException) {
         JvmStackFrame(null, it.lineNumber)
      } catch (_: LinkageError) {
         JvmStackFrame(null, it.lineNumber)
      }
      try {
         frame.takeIf(predicate)
      } catch (_: ReflectiveOperationException) {
         null
      } catch (_: LinkageError) {
         null
      }
   }
}
