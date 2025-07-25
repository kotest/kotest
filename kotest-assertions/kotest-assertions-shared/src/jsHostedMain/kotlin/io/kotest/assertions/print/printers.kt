package io.kotest.assertions.print

actual fun <A : Any> platformPrint(a: A): Print<A>? = null

internal actual fun Any?.printType() = this?.let { it::class.simpleName } ?: ""

actual fun <A : Any> dataClassPrint(): Print<A> = ToStringPrint
