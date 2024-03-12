package io.kotest.assertions.print

internal actual fun Any?.printType() = this?.let { it::class.js.name } ?: ""
