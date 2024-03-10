@file:JvmName("stacktracesjvm")

package io.kotest.mpp

actual val stacktraces: StackTraces = object : StackTraces {

   override fun throwableLocation(t: Throwable, n: Int): List<String> {
      // stackTrace can error if the throwable is mocked
      return try {
         (t.cause ?: t).stackTrace?.dropWhile {
            it.className.startsWith("io.kotest")
         }?.take(n)?.map { it.toString() } ?: emptyList()
      } catch (e: Throwable) {
         emptyList()
      }
   }

   override fun <T : Throwable> cleanStackTrace(t: T): T {
      if (shouldRemoveKotestElementsFromStacktrace) {
         // stackTrace can error if the throwable is mocked
         try {
            t.stackTrace = UserStackTraceConverter.getUserStacktrace(t.stackTrace)
         } catch (e: Throwable) {
         }
      }
      return t
   }

   override fun root(t: Throwable): Throwable {
      // cause can error if the throwable is mocked
      return try {
         val cause = t.cause
         if (cause == null) t else root(cause)
      } catch (e: Throwable) {
         t
      }
   }
}
