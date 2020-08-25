package io.kotest.fp

sealed class Option<out T> {

   data class Some<T>(val value: T) : Option<T>()

   object None : Option<Nothing>()

   fun isDefined(): Boolean = this is Some
   fun isEmpty(): Boolean = this is None

   inline fun <R> fold(ifEmpty: () -> R, ifDefined: (T) -> R): R = when (this) {
      is Some -> ifDefined(this.value)
      is None -> ifEmpty()
   }

   inline fun <U> map(f: (T) -> U): Option<U> = when (this) {
      is None -> this
      is Some -> f(this.value).some()
   }

   fun forEach(f: (T) -> Unit) = fold({}, { f(it) })

   fun orNull(): T? = fold({ null }, { it })
}

fun <T> Option<T>.getOrElse(t: T): T = fold({ t }, { it })

fun <T> Option<T>.getOrElse(f: () -> T): T = fold({ f() }, { it })

fun <T> Option<T>.orElse(other: Option<T>): Option<T> = when (this) {
   is Option.None -> other
   else -> this
}

fun <T> T.some(): Option<T> = Option.Some(this)

fun <T> Collection<T>.firstOption(): Option<T> = if (this.isEmpty()) Option.None else Option.Some(first())

fun <T> T?.toOption(): Option<T> = if (this == null) Option.None else Option.Some(this)
