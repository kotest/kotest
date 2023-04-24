package io.kotest.assertions.print

// native doesn't support full reflection so we just need to delegate to the instances toString
actual fun <A : Any> dataClassPrint(): Print<A> = ToStringPrint
actual fun <A : Any> platformPrint(a: A): Print<A>? = null

internal actual fun Any?.printType() = ""
