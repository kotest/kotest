package io.kotest.matchers

import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.eq.EqResult.Equal

internal interface EqMatcherResult : MatcherResult {
   fun failureOrNull(): Throwable?

   companion object {
      operator fun invoke(
         result: EqResult,
         failureMessageFn: (error: Throwable?) -> String,
         negatedFailureMessageFn: (error: Throwable?) -> String,
      ): EqMatcherResult = object : EqMatcherResult {
         override fun failureOrNull(): Throwable? = result.failureOrNull()
         override fun passed(): Boolean = result is Equal
         override fun failureMessage(): String = failureMessageFn(result.failureOrNull())
         override fun negatedFailureMessage(): String = negatedFailureMessageFn(result.failureOrNull())
      }
   }
}
