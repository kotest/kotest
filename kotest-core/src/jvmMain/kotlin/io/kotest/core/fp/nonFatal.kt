package io.kotest.core.fp

actual fun nonFatal(t: Throwable): Boolean = when (t) {
   is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> false
   else -> true
}
