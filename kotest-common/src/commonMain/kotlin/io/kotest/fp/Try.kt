package io.kotest.fp

sealed class Try<out T> {

   data class Success<T>(val value: T) : Try<T>()
   data class Failure(val error: Throwable) : Try<Nothing>()

   companion object {
      operator fun invoke(t: Throwable): Try<Unit> = Failure(t)
      inline operator fun <T> invoke(f: () -> T): Try<T> = try {
         Success(f())
      } catch (e: Throwable) {
         if (nonFatal(e)) Failure(e) else throw e
      }
   }

   inline fun <U> map(f: (T) -> U): Try<U> = flatMap { Try { f(it) } }

   fun isSuccess() = this is Success
   fun isFailure() = this is Failure

   fun getOrThrow(): T = when (this) {
      is Failure -> throw error
      is Success -> value
   }

   inline fun <U> flatMap(f: (T) -> Try<U>): Try<U> = when (this) {
      is Failure -> this
      is Success -> f(value)
   }

   inline fun <R> fold(ifFailure: (Throwable) -> R, ifSuccess: (T) -> R): R = when (this) {
      is Failure -> ifFailure(this.error)
      is Success -> ifSuccess(this.value)
   }

   inline fun onFailure(f: (Throwable) -> Unit): Try<T> = when (this) {
      is Success -> this
      is Failure -> {
         f(this.error)
         this
      }
   }

   inline fun onSuccess(f: (T) -> Unit): Try<T> = when (this) {
      is Success -> {
         f(this.value)
         this
      }
      is Failure -> this
   }

   fun toOption(): Option<T> = fold({ Option.None }, { Option.Some(it) })

   inline fun mapFailure(f: (Throwable) -> Throwable) = fold({ f(it).failure() }, { it.success() })

   fun getOrNull(): T? = fold({ null }, { it })

   fun errorOrNull(): Throwable? = fold({ it }, { null })
}

fun <T> Try<Try<T>>.flatten(): Try<T> = when (this) {
   is Try.Success -> this.value
   is Try.Failure -> this
}

inline fun <U, T : U> Try<T>.getOrElse(f: (Throwable) -> U): U = when (this) {
   is Try.Success -> this.value
   is Try.Failure -> f(this.error)
}

inline fun <U, T : U> Try<T>.recoverWith(f: (Throwable) -> Try<U>): Try<U> = when (this) {
   is Try.Success -> this
   is Try.Failure -> f(this.error)
}

fun <U, T : U> Try<T>.recover(f: (Throwable) -> U): Try<U> = when (this) {
   is Try.Success -> this
   is Try.Failure -> Try { f(this.error) }
}

fun <T> T.success(): Try<T> = Try.Success(this)
fun Throwable.failure(): Try<Nothing> =
   Try.Failure(this)
