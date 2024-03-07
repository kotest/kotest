package io.kotest.assertions.print

// js doesn't support full reflection so we just need to delegate to the instances toString
actual fun <A : Any> dataClassPrint(): Print<A> = ToStringPrint
actual fun <A : Any> platformPrint(a: A): Print<A>? = null

internal actual fun Any?.printType() = this?.let { it::class.js.name } ?: ""
