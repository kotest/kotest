package io.kotest.fp

inline fun <T> T?.foreach(f: (T) -> Unit) {
   if (this != null) f(this)
}
