package io.kotest.matchers.future

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.CompletableFuture

fun <T> CompletableFuture<T>.shouldBeCompletedExceptionally() = this shouldBe completedExceptionally<T>()
fun <T> CompletableFuture<T>.shouldNotBeCompletedExceptionally() = this shouldNotBe completedExceptionally<T>()
fun <T> completedExceptionally() = object : Matcher<CompletableFuture<T>> {
   override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
         value.isCompletedExceptionally,
         { "Future should be completed exceptionally" },
         { errorMessageForFailedFuture(value) })
}

internal fun errorMessageForFailedFuture(failedFuture: CompletableFuture<*>): String {
   val exception = failedFuture.runCatching { get() }.exceptionOrNull()
      ?: error("Future completed exceptionally, but get() did not throw an exception")
   return "Future should not be completed exceptionally, but it failed with ${exception.cause}"
}

fun <T> CompletableFuture<T>.shouldBeCompleted() = this shouldBe completed<T>()
fun <T> CompletableFuture<T>.shouldNotBeCompleted() = this shouldNotBe completed<T>()
fun <T> completed() = object : Matcher<CompletableFuture<T>> {
   override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
         value.isDone,
         { "Future should be completed" },
         {
            "Future should not be completed"
         })
}

fun <T> CompletableFuture<T>.shouldBeCancelled() = this shouldBe cancelled<T>()
fun <T> CompletableFuture<T>.shouldNotBeCancelled() = this shouldNotBe cancelled<T>()
fun <T> cancelled() = object : Matcher<CompletableFuture<T>> {
   override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
         value.isCancelled,
         { "Future should be completed" },
         {
            "Future should not be completed"
         })
}

infix fun CompletableFuture<*>.shouldCompleteExceptionallyWith(throwable: Throwable) =
   this should completeExceptionallyWith(throwable)

infix fun CompletableFuture<*>.shouldNotCompleteExceptionallyWith(throwable: Throwable) =
   this shouldNot completeExceptionallyWith(throwable)

internal fun completeExceptionallyWith(throwable: Throwable) = object : Matcher<CompletableFuture<*>> {
   override fun test(value: CompletableFuture<*>): MatcherResult {
      val exception = value.runCatching { get() }.exceptionOrNull()
      return MatcherResult(
         exception != null && exception.cause == throwable,
         { errorMessageForTestFailure(exception?.cause, throwable) },
         { "Expected future not to fail with ${exception?.cause}, but it did fail with it." }
      )
   }
}

internal fun errorMessageForTestFailure(actualException: Throwable?, expectedException: Throwable): String {
   if (actualException == null) {
      return "Expected future to fail with $expectedException, but it did not failed with any exception"
   }
   return "Expected future to fail with $expectedException, but it failed with $actualException"
}
