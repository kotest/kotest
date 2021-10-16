package io.kotest.fp

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> {
   return fold({ fn(it) }, { Result.failure(it) })
}

fun <A> Result<A>.mapError(f: (Throwable) -> Throwable): Result<A> =
   fold({ Result.success(it) }, { Result.failure(f(this.exceptionOrNull()!!)) })
