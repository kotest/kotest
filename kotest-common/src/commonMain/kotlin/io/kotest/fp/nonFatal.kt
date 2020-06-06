package io.kotest.fp

fun nonFatal(t: Throwable): Boolean = when (t::class.simpleName) {
   "VirtualMachineError", "ThreadDeath", "InterruptedException" -> false
   else -> true
}

