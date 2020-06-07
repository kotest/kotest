@file:JvmName("stacktracesjvm")

package io.kotest.mpp

actual val stacktraces: StackTraces = object : StackTraces {

   override fun throwableLocation(t: Throwable): String? {
      return throwableLocation(t, 1)?.firstOrNull()
   }

   override fun throwableLocation(t: Throwable, n: Int): List<String>? {
      return (t.cause ?: t).stackTrace?.dropWhile {
         it.className.startsWith("io.kotest")
      }?.take(n)?.map { it.toString() } ?: emptyList()
   }

   override fun <T : Throwable> cleanStackTrace(throwable: T): T {
      if (shouldRemoveKotestElementsFromStacktrace) {
         throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
      }
      return throwable
   }

   override fun root(throwable: Throwable): Throwable {
      val cause = throwable.cause
      return if (cause == null) throwable else root(cause)
   }
}
