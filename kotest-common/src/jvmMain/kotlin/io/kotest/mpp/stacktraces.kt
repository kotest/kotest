@file:JvmName("stacktracesjvm")

package io.kotest.mpp

actual object StackTraces {

   actual fun root(throwable: Throwable): Throwable {
      val cause = throwable.cause
      return if (cause == null) throwable else root(cause)
   }

   actual fun Throwable.throwableLocation(): String? {
      return (cause ?: this).stackTrace?.firstOrNull {
         !it.className.startsWith("io.kotest")
      }?.toString()
   }

   actual fun Throwable.location(n: Int): List<String>? {
      return (cause ?: this).stackTrace?.dropWhile {
         it.className.startsWith("io.kotest")
      }?.take(n)?.map { it.toString() } ?: emptyList()
   }

   actual fun <T : Throwable> cleanStackTrace(throwable: T): T {
      if (shouldRemoveKotestElementsFromStacktrace) {
         throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
      }
      return throwable
   }
}
