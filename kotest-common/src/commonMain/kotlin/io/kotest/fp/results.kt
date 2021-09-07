package io.kotest.fp

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> {
   return fold({ fn(it) }, { Result.failure(it) })
}
