package io.kotest.common.stacktrace

actual val stacktraces: StackTraces = BasicStackTraces

object BasicStackTraces : StackTraces {
   override fun throwableLocation(t: Throwable, n: Int): List<String>? = null
   override fun <T : Throwable> cleanStackTrace(t: T): T = t
   override fun root(t: Throwable): Throwable = t
}
