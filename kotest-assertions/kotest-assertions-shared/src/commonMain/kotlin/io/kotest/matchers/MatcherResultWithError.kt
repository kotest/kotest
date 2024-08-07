package io.kotest.matchers

import io.kotest.common.KotestInternal

@KotestInternal
interface MatcherResultWithError : MatcherResult {

   val error: Throwable?

   companion object {
      operator fun invoke(
         error: Throwable?,
         passed: Boolean,
         failureMessageFn: (error: Throwable?) -> String,
         negatedFailureMessageFn: (error: Throwable?) -> String,
      ): MatcherResultWithError = object : MatcherResultWithError {

         override val error: Throwable? = error

         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessageFn(error)
         override fun negatedFailureMessage(): String = negatedFailureMessageFn(error)
      }
   }
}
