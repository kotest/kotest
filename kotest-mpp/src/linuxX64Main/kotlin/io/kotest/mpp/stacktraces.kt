package io.kotest.mpp

actual object StackTraces {
   actual fun Throwable.throwableLocation(): String? = null
   actual fun <T : Throwable> cleanStackTrace(throwable: T): T = throwable
}
