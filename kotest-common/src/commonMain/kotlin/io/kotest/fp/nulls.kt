package io.kotest.fp

fun <T, U> T?.fmap(f: (T) -> U): U? = if (this == null) null else f(this)

fun <T> T?.foreach(f: (T) -> Unit) {
   if (this != null) f(this)
}
