package io.kotest.core.fp

sealed class Option<out T> {
   data class Some<T>(val value: T) : Option<T>()
   object None : Option<Nothing>()

   inline fun <R> fold(ifEmpty: () -> R, ifDefined: (T) -> R): R = when (this) {
      is Some -> ifDefined(this.value)
      is None -> ifEmpty()
   }
}

fun <T> Option<T>.getOrElse(t: T): T = fold({ t }, { it })

fun <T> Option<T>.getOrElse(f: () -> T): T = fold({ f() }, { it })

fun <T> Option<T>.orElse(opt: Option<T>): Option<T> = when (this) {
   is Option.None -> opt
   else -> this
}

fun <T> Option<T>.orElse(f: () -> Option<T>): Option<T> = when (this) {
   is Option.None -> f()
   else -> this
}

fun <T> T.some(): Option<T> = Option.Some(this)

fun <T> T?.toOption(): Option<T> = if (this == null) Option.None else Option.Some(this)
