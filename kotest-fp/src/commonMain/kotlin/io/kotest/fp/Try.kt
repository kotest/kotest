package io.kotest.fp

sealed class Try<out T> {

   data class Success<T>(val value: T) : Try<T>()
   data class Failure(val error: Throwable) : Try<Nothing>()

   companion object {
      inline operator fun <T> invoke(f: () -> T): Try<T> = try {
         Success(f())
      } catch (e: Throwable) {
         println(e)
         if (nonFatal(e)) Failure(e) else throw e
      }
   }

   inline fun <U> map(crossinline f: (T) -> U): Try<U> = flatMap {
      Try { f(it) }
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

   fun toOption(): Option<T> = fold({ Option.None }, {
      Option.Some(
         it
      )
   })
}

fun <T> T.success() = Try.Success(this)

expect fun nonFatal(t: Throwable): Boolean
