@file:JvmName("stacktracesjvm")

package io.kotest.mpp

actual object StackTraces {
   actual fun Throwable.throwableLocation(): String? {
      return (cause ?: this).stackTrace?.firstOrNull {
         !it.className.startsWith("io.kotest")
      }?.toString()
   }

   actual fun <T : Throwable> cleanStackTrace(throwable: T): T {
      if (shouldRemoveKotestElementsFromStacktrace) {
         throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
      }
      return throwable
   }
}
