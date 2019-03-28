package io.kotlintest.matchers.string

// Only supporting 0-9 for now, waiting for official support in Kotlin JS for `isDigit`
actual fun Char.isDigit(): Boolean = this in '0'..'9'