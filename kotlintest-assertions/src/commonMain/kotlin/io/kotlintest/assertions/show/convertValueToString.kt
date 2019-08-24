package io.kotlintest.assertions.show

@Deprecated("Use the Show typeclass directly", ReplaceWith("value.show()", "io.kotlintest.assertions.show.Show"))
fun convertValueToString(value: Any?): String = value.show()
