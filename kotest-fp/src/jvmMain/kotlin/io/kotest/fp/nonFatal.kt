package io.kotest.fp

actual fun nonFatal(t: Throwable): Boolean = when (t) {
   is VirtualMachineError, is ThreadDeath, is InterruptedException -> false
   else -> true
}
