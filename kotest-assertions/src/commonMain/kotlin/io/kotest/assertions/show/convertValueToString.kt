package io.kotest.assertions.show

@Deprecated("Use the Show typeclass directly", ReplaceWith("value.show()", "io.kotest.assertions.show.Show"))
fun convertValueToString(value: Any?): String = value.show()
