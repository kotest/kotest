package io.kotest.core.source

private val stackWalker: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

internal actual fun findFirstStackFrame(
   excludeDataTest: Boolean,
   predicate: (JvmStackFrame) -> Boolean,
): JvmStackFrame? {
   return stackWalker.walk { frames ->
      frames
         .filter { !SourceRefUtils.isExcludedFrame(it.className, excludeDataTest) }
         .map { JvmStackFrame(it.declaringClass, it.lineNumber) }
         .filter(predicate)
         .findFirst()
   }.orElse(null)
}
