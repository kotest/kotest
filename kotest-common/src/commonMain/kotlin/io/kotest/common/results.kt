package io.kotest.common

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> {
   return fold({ fn(it) }, { Result.failure(it) })
}

@KotestInternal
fun <A> Result<Result<A>>.flatten(): Result<A> {
   return when {
      isSuccess -> getOrThrow()
      else -> Result.failure(exceptionOrNull()!!)
   }
}


fun <A> Result<A>.mapError(f: (Throwable) -> Throwable): Result<A> =
   fold({ Result.success(it) }, { Result.failure(f(this.exceptionOrNull()!!)) })

fun <A> List<Result<A>>.collect(f: (List<Throwable>) -> Throwable): Result<List<A>> {
   val (errors, successes) = this.partition { it.isFailure }
   return if (errors.isNotEmpty()) Result.failure(f(errors.map { it.exceptionOrNull()!! }))
   else Result.success(successes.map { it.getOrThrow() })
}
