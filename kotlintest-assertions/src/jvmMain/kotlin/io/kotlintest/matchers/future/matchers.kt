package io.kotlintest.matchers.future

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.util.concurrent.CompletableFuture

fun <T> CompletableFuture<T>.shouldBeCompletedExceptionally() = this shouldBe completedExceptionally<T>()
fun <T> CompletableFuture<T>.shouldNotBeCompletedExceptionally() = this shouldNotBe completedExceptionally<T>()
fun <T> completedExceptionally() = object : Matcher<CompletableFuture<T>> {
  override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
          value.isCompletedExceptionally,
          "Future should be completed exceptionally",
          "Future should not be completed exceptionally"
      )
}


fun <T> CompletableFuture<T>.shouldBeCompleted() = this shouldBe completed<T>()
fun <T> CompletableFuture<T>.shouldNotBeCompleted() = this shouldNotBe completed<T>()
fun <T> completed() = object : Matcher<CompletableFuture<T>> {
  override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
          value.isDone,
          "Future should be completed",
          "Future should not be completed"
      )
}

fun <T> CompletableFuture<T>.shouldBeCancelled() = this shouldBe cancelled<T>()
fun <T> CompletableFuture<T>.shouldNotBeCancelled() = this shouldNotBe cancelled<T>()
fun <T> cancelled() = object : Matcher<CompletableFuture<T>> {
  override fun test(value: CompletableFuture<T>): MatcherResult =
      MatcherResult(
          value.isCancelled,
          "Future should be completed",
          "Future should not be completed"
      )
}
